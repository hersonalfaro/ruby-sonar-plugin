/*
 * Sonar Ruby Plugin
 * Copyright (C) 2009-2016 SonarSource SA
 * mailto:contact AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.plugins.ruby;

import java.io.BufferedReader;
import java.io.Closeable;
/*
 * SonarQube Ruby Plugin
 * Copyright (C) 2013-2017 SonarSource SA
 * mailto:contact AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.plaf.basic.BasicSplitPaneUI.KeyboardUpLeftHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import org.apache.commons.io.FileUtils;
//import org.apache.commons.io.IOUtils;
import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.batch.fs.FilePredicate;
import org.sonar.api.batch.fs.FilePredicates;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.fs.TextPointer;
import org.sonar.api.batch.fs.TextRange;
import org.sonar.api.batch.sensor.highlighting.NewHighlighting;
import org.sonar.api.batch.sensor.highlighting.TypeOfText;
import org.sonar.api.config.Settings;
//mport org.sonar.api.internal.google.common.collect.Lists;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.resources.Project;
import org.sonar.plugins.ruby.core.Ruby;
import org.sonar.plugins.ruby.core.RubyFile;
import org.sonar.plugins.ruby.core.RubyPackage;
import org.sonar.plugins.ruby.core.RubyRecognizer;
import org.sonar.plugins.ruby.parsers.CommentCountParser;
import org.sonar.plugins.ruby.simplecovrcov.SimpleCovRcovJsonParserImpl;
import org.sonar.squidbridge.measures.Metric;
import org.sonar.squidbridge.text.Source;


//import com.google.common.collect.Lists;

public class RubySensor implements Sensor {
	// private ModuleFileSystem moduleFileSystem;
	private Settings settings;
	private FileSystem fs;
	private static final Logger LOG = LoggerFactory.getLogger(RubySensor.class);

	public RubySensor(Settings settings, FileSystem fs) {
		this.settings = settings;
		this.fs = fs;
	}

	@Override
	public boolean shouldExecuteOnProject(Project project) {
		// This sensor is executed only when there are Ruby files
		return fs.hasFiles(fs.predicates().hasLanguage("ruby"));
	}

	public void analyse(Project project, SensorContext context) {
		computeBaseMetrics(context, project);
	}

	public static String readFileToString(File path, String encoding) throws IOException {
		return readFileToString(path, Charset.forName(encoding));

	}

	public static String readFileToString(File path, Charset encoding) throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path.toURI()));
		return new String(encoded, encoding);
	}

	protected void computeBaseMetrics(SensorContext sensorContext, Project project) {
		Reader reader = null;
		FilePredicate filePredicate = fs.predicates().hasLanguage("ruby");
		List<InputFile> sourceFiles = new ArrayList<>();// Lists.newArrayList(fs.inputFiles(filePredicate));
		Iterator<InputFile> iter = fs.inputFiles(filePredicate).iterator();
		while (iter.hasNext()) {
			sourceFiles.add(iter.next());
		}

		Set<RubyPackage> packageList = new HashSet<RubyPackage>();
		for (InputFile rubyFile : sourceFiles) {
			try {
				File fileRuby = rubyFile.file();

				String sourceString = readFileToString(fileRuby, fs.encoding().name());
				reader = new StringReader(sourceString);
				RubyFile resource = new RubyFile(fileRuby, sourceFiles);
				Source source = new Source(reader, new RubyRecognizer());
				packageList.add(new RubyPackage(resource.getParent().getKey()));

				sensorContext.saveMeasure(rubyFile, CoreMetrics.NCLOC,
						(double) source.getMeasure(Metric.LINES_OF_CODE));
				int numCommentLines = CommentCountParser.countLinesOfComment(fileRuby);

				sensorContext.saveMeasure(rubyFile, CoreMetrics.COMMENT_LINES, (double) numCommentLines);
				sensorContext.saveMeasure(rubyFile, CoreMetrics.FILES, 1.0);
				sensorContext.saveMeasure(rubyFile, CoreMetrics.CLASSES, 1.0);

			} catch (Exception e) {
				throw new IllegalStateException("Error computing base metrics for project.", e);
			} finally {
				closeQuietly(reader);
			}
		}

		for (InputFile rubyFile : sourceFiles) {
			try {
				LOG.info("Highlighting " + rubyFile.relativePath());
				NewHighlighting highlighting = sensorContext.newHighlighting();
				highlighting.onFile(rubyFile);
				highlightWords(highlighting, rubyFile, Ruby.RUBY_KEYWORDS_ARRAY, TypeOfText.KEYWORD);
				highlightWords(highlighting, rubyFile, Ruby.RUBY_RESERVED_VARIABLES_ARRAY, TypeOfText.KEYWORD_LIGHT);
				 
				highlightLines(highlighting, rubyFile, new String[]{"^\\s*#.*$"} , TypeOfText.COMMENT);
				

				highlighting.save();
			} catch (Exception e) {
				throw new IllegalStateException("Error highlighting for project.", e);
			}
		}
	}

	public static void highlightWords(NewHighlighting highlighting, InputFile file, String[] arr,
			TypeOfText typeOfText) {

		for (String keyword : arr) {
			Pattern pattern = Pattern.compile("\\b" + keyword + "\\b");
			try (BufferedReader br = new BufferedReader(new FileReader(file.file()))) {
				String line;
				int lineCount = 1;
				while ((line = br.readLine()) != null) {
					// process the line.

					// TODO : improve ugly hack to avoid comment words from
					// actual code words
					if (!line.matches("^\\s*#.*$")) {
					 
					 

					Matcher matcher = pattern.matcher(line);
					while (matcher.find()) {
						int index = matcher.start();
						//LOG.info(String.format(
						//		"highlight keyword [%s] for line [%d] from offset[%d] to [%d] for contents [%s]",
						//		keyword, lineCount, index, index + keyword.length(), line));
						highlighting.highlight(lineCount, index, lineCount, index + keyword.length(), typeOfText);
					}
					}
					lineCount++;
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	public static void highlightLines(NewHighlighting highlighting, InputFile file, String[] arrOfRegex,
			TypeOfText typeOfText) {

		for (String regex : arrOfRegex) {
			Pattern pattern = Pattern.compile(regex);
			try (BufferedReader br = new BufferedReader(new FileReader(file.file()))) {
				String line;
				int lineCount = 1;
				while ((line = br.readLine()) != null) {
					// process the line.

					Matcher matcher = pattern.matcher(line);
					if (matcher.matches()) {
						int start = matcher.start();
						int end = matcher.end();
						//LOG.info(String.format(
						//		"highlight regex [%s] for line [%d] from offset[%d] to [%d] for contents [%s]", regex,
						//		lineCount, start, end, line));
						highlighting.highlight(lineCount, start, lineCount, end, typeOfText);
					}

					lineCount++;
				}
			} catch (FileNotFoundException e) {

				e.printStackTrace();
			} catch (IOException e) {

				e.printStackTrace();
			}

		}
	}

	public static void closeQuietly(Closeable closeable) {
		try {
			if (closeable != null) {
				closeable.close();
			}
		} catch (IOException ioe) {
			// ignore
		}
	}

	@Override
	public String toString() {
		return getClass().getSimpleName();
	}
}
