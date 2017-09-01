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
import org.sonar.api.resources.Language;
import org.sonar.api.resources.Qualifiers;
import org.sonar.api.resources.Resource;
import org.sonar.api.resources.Scopes;
import org.sonar.api.utils.WildcardPattern;

@SuppressWarnings("rawtypes")
public class RubyPackage extends Resource
{
    public static final String DEFAULT_PACKAGE_NAME = "[default]";

    public RubyPackage(String key)
    {
        super();
        setKey(StringUtils.defaultIfEmpty(StringUtils.trim(key), DEFAULT_PACKAGE_NAME));
    }

    public boolean matchFilePattern(String antPattern)
    {
        String patternWithoutFileSuffix = StringUtils.substringBeforeLast(antPattern, ".");
        WildcardPattern matcher = WildcardPattern.create(patternWithoutFileSuffix, ".");
        return matcher.match(getKey());
    }

    public String getDescription()
    {
        return null;
    }

    public String getName()
    {
        return getKey();
    }

    public Resource getParent()
    {
        return null;
    }

    public String getLongName()
    {
        return null;
    }

    public Language getLanguage()
    {
        return Ruby.INSTANCE;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this).append("key", getKey()).toString();
    }

    @Override
    public String getScope()
    {
        return Scopes.DIRECTORY;
    }

    @Override
    public String getQualifier()
    {
        return Qualifiers.MODULE;
    }
}
