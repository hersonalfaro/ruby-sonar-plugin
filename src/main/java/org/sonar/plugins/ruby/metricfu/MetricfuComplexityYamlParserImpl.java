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
package org.sonar.plugins.ruby.metricfu;
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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import org.sonar.api.internal.google.common.base.Throwables;
import org.yaml.snakeyaml.Yaml;

//import com.google.common.base.Throwables;

public class MetricfuComplexityYamlParserImpl implements
    MetricfuComplexityYamlParser {
  private static final Logger LOG = LoggerFactory
                                      .getLogger(MetricfuComplexityYamlParser.class);

  @SuppressWarnings("unchecked")
  public List<RubyFunction> parseFunctions(String fileNameFromModule, File resultsFile, String complexityType) throws IOException
  {
//    String fileString = FileUtils.readFileToString(resultsFile, "UTF-8");
    InputStream resultsStream = new FileInputStream(resultsFile);

    LOG.debug("MetricfuComplexityYamlParserImpl: Start start parse of metrics_fu YAML");

    // remove ":hotspots:" section of the yaml so snakeyaml can parse it
    // correctly, snakeyaml throws an error with that section intact
    // Will remove if metric_fu metric filtering works for hotspots in the
    // future
    
// Removing this as it seems to be working correctly with current versions of dependencies
//    int hotSpotIndex = fileString.indexOf(":hotspots:");
//    if (hotSpotIndex >= 0)
//    {
//      String stringToRemove = fileString.substring(hotSpotIndex, fileString.length());
//      fileString = StringUtils.remove(fileString, stringToRemove);
//    }

    Yaml yaml = new Yaml();
    Map<String, Object> metricfuResult = new HashMap();
    try {
//      metricfuResult = (Map<String, Object>) yaml.loadAs(fileString, Map.class);
      metricfuResult = (Map<String, Object>) yaml.load(resultsStream);
      
      Map<String, Object> saikuroResult = (Map<String, Object>) metricfuResult.get(":saikuro");
      Map<String, Object> caneResult = (Map<String, Object>) metricfuResult.get(":cane");

      if ("Saikuro".equals(complexityType) && null != saikuroResult) {
        return analyzeSaikuro(fileNameFromModule, metricfuResult, saikuroResult);
      } else if ("Cane".equals(complexityType) && null != caneResult) {
        return analyzeCane(fileNameFromModule, metricfuResult, caneResult);
      } else {
        LOG.warn("No data found for complexity type " + complexityType);
        return new ArrayList<RubyFunction>();
      }
    } catch (Exception e) {
    //  LOG.error(Throwables.getStackTraceAsString(e));
      throw new IOException("Failure parsing YAML results", e);
    }
    
  }

  private List<RubyFunction> analyzeSaikuro(String fileNameFromModule, Map<String, Object> metricfuResult,
      Map<String, Object> saikuroResult) {
    
    List<RubyFunction> rubyFunctionsForFile = new ArrayList<RubyFunction>();
    Map<String, Object> fileInfoToWorkWith = new HashMap<String, Object>();

      LOG.debug("MetricfuComplexityYamlParserImpl: parsing results from saikuro");
      ArrayList<Map<String, Object>> saikuroFilesResult = (ArrayList<Map<String, Object>>) saikuroResult.get(":files");

      for (Map<String, Object> fileInfo : saikuroFilesResult)
      {
        String fileNameFromResults = (String) fileInfo.get(":filename");

        if (fileNameFromResults.contains(fileNameFromModule))
        {
          fileInfoToWorkWith = fileInfo;
          break;
        }
      }

    if (fileInfoToWorkWith.size() == 0)
    {
      // file has no methods returning empty function list
      return new ArrayList<RubyFunction>();
    }

    ArrayList<Map<String, Object>> classesInfo = (ArrayList<Map<String, Object>>) fileInfoToWorkWith.get(":classes");

    for (Map<String, Object> classInfo : classesInfo)
    {
      ArrayList<Map<String, Object>> methods = (ArrayList<Map<String, Object>>) classInfo.get(":methods");

      for (Map<String, Object> method : methods)
      {
        RubyFunction rubyFunction = new RubyFunction();
        rubyFunction.setName((String) method.get(":name"));
        rubyFunction.setComplexity((Integer) method.get(":complexity"));
        rubyFunction.setLine((Integer) method.get(":lines"));

        rubyFunctionsForFile.add(rubyFunction);
      }
    }
    return rubyFunctionsForFile;
  }

  private List<RubyFunction> analyzeCane(String fileNameFromModule, Map<String, Object> metricfuResult,
      Map<String, Object> caneResult) {
    
    List<RubyFunction> rubyFunctionsForFile = new ArrayList<RubyFunction>();
    Map<String, Object> fileInfoToWorkWith = new HashMap<String, Object>();

      LOG.debug("MetricfuComplexityYamlParserImpl: parsing results from cane");
      Map<String, Object> caneViolationsResult = (Map<String, Object>) caneResult.get(":violations");
      ArrayList<Map<String, Object>> caneAbcComplexityResult = (ArrayList<Map<String, Object>>) caneViolationsResult.get(":abc_complexity");

      for (Map<String, Object> fileInfo : caneAbcComplexityResult)
      {
        String fileNameFromResults = (String) fileInfo.get(":file");

        if (fileNameFromResults.contains(fileNameFromModule))
        {
          fileInfoToWorkWith = fileInfo;
          break;
        }
      }

    if (fileInfoToWorkWith.size() == 0)
    {
      // file has no methods returning empty function list
      return new ArrayList<RubyFunction>();
    }

    RubyFunction rubyFunction = new RubyFunction();
    LOG.debug("analyzeCane: fileInfoToWorkWith - " + fileInfoToWorkWith);
    rubyFunction.setName((String) fileInfoToWorkWith.get(":method"));
    rubyFunction.setComplexity(Integer.parseInt( (String)(fileInfoToWorkWith.get(":complexity"))));
    rubyFunctionsForFile.add(rubyFunction);
    
    return rubyFunctionsForFile;
  }
}
