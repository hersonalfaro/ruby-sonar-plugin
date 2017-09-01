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
import org.sonar.api.resources.AbstractLanguage;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

/**
 * Ruby language implementation
 *
 */
public class LanguageRuby extends AbstractLanguage {

  public static final LanguageRuby INSTANCE = new LanguageRuby("ruby");

    public LanguageRuby(String key) {
    super(key);
  }


    /**
     * Java key
     */
    public static final String KEY = "ruby";

    /**
     * Java name
     */
    public static final String NAME = "Ruby";

    /**
     * Default package name for classes without package def
     */
    public static final String DEFAULT_PACKAGE_NAME = "[default]";

    /**
     * Java files knows suffixes
     */
    public static final String[] SUFFIXES = {".rb", ".ruby"};

    /**
     * Default constructor
     */
    public LanguageRuby() {
      super(KEY, NAME);
    }

    /**
     * {@inheritDoc}
     *
     * @see AbstractLanguage#getFileSuffixes()
     */
    public String[] getFileSuffixes() {
      return SUFFIXES;
    }

    public static boolean isRubyFile(java.io.File file) {
      String suffix = "." + StringUtils.lowerCase(StringUtils.substringAfterLast(file.getName(), "."));
      return ArrayUtils.contains(SUFFIXES, suffix);
    }
    
}
