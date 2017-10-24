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
package org.sonar.plugins.ruby.core;
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
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.resources.Language;
import org.sonar.api.resources.Qualifiers;
import org.sonar.api.resources.Resource;
import org.sonar.api.resources.Scopes;
import org.sonar.api.scan.filesystem.PathResolver;
import org.sonar.api.scan.filesystem.PathResolver.RelativePath;
import org.sonar.api.utils.WildcardPattern;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RubyFile extends Resource
{
    /**
   * 
   */
    private static final long serialVersionUID = 1L;
    private String filename;
    private String longName;
    private String packageKey;
    private RubyPackage parent = null;

    public RubyFile(File file, List<InputFile> sourceDirs)
    {
        super();

        if (file == null)
        {
            throw new IllegalArgumentException("File cannot be null");
        }

        String dirName = null;
        this.filename = StringUtils.substringBeforeLast(file.getName(), ".");

        this.packageKey = RubyPackage.DEFAULT_PACKAGE_NAME;

        if (sourceDirs != null)
        {
            PathResolver resolver = new PathResolver();
            Collection<File> colSrcDirs = toFileCollection(sourceDirs);
            RelativePath relativePath = resolver.relativePath(colSrcDirs, file);
            if (relativePath != null)
            {
                dirName = relativePath.dir().toString();

                this.filename = StringUtils.substringBeforeLast(relativePath.path(), ".");

                if (dirName.indexOf(File.separator) >= 0)
                {
                    this.packageKey = StringUtils.strip(dirName, File.separator);
                    this.packageKey = StringUtils.replace(this.packageKey, File.separator, ".");
                    this.packageKey = StringUtils.substringAfterLast(this.packageKey, ".");
                }
            }
        }

        String key = new StringBuilder().append(this.packageKey).append(".").append(this.filename).toString();
        this.longName = key;

        setKey(key);
    }

    public RubyPackage getParent()
    {
        if (parent == null)
        {
            parent = new RubyPackage(packageKey);
        }
        return parent;
    }

    public String getDescription()
    {
        return null;
    }

    public Language getLanguage()
    {
        return Ruby.INSTANCE;
    }

    public String getName()
    {
        return filename;
    }

    public String getLongName()
    {
        return longName;
    }

    public String getScope()
    {
        return Scopes.FILE;
    }

    public String getQualifier()
    {
        return Qualifiers.FILE;
    }

    public boolean matchFilePattern(String antPattern)
    {
        String patternWithoutFileSuffix = StringUtils.substringBeforeLast(antPattern, ".");
        WildcardPattern matcher = WildcardPattern.create(patternWithoutFileSuffix, ".");
        String key = getKey();
        return matcher.match(key);
    }

    public Collection<File> toFileCollection (List<InputFile> inputFiles){
      Collection<File> listOfFiles = new ArrayList<File>();
      for (InputFile afile : inputFiles) {
          listOfFiles.add( afile.file());
      }   
      return listOfFiles;
    }
    
    @Override
    public String toString() {
      return "\nRubyFile [getParent()=" + getParent() + ", getLanguage()=" + getLanguage() + ", getName()=" + getName()
          + ", getLongName()=" + getLongName() + ", getScope()=" + getScope() + ", getQualifier()=" + getQualifier()
          + ", getKey()=" + getKey() + ", getId()=" + getId() + ", getPath()=" + getPath() + ", getEffectiveKey()="
          + getEffectiveKey() + ", isExcluded()=" + isExcluded() + "]\n";
    }

//    @Override
//    public String toString()
//    {
//        return new ToStringBuilder(this).append("key", getKey()).append("package", packageKey).append("longName", longName).toString();
//    }

}
