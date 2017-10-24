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
package org.sonar.plugins.ruby;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.sonar.api.CoreProperties;
import org.sonar.api.Properties;
import org.sonar.api.PropertyType;
import org.sonar.api.SonarPlugin;
import org.sonar.api.config.PropertyDefinition;
import org.sonar.api.resources.Qualifiers;
import org.sonar.plugins.ruby.core.Ruby;
import org.sonar.plugins.ruby.core.profiles.SonarWayProfile;
import org.sonar.plugins.ruby.metricfu.MetricfuComplexitySensor;
import org.sonar.plugins.ruby.metricfu.MetricfuComplexityYamlParserImpl;
import org.sonar.plugins.ruby.simplecovrcov.SimpleCovRcovJsonParserImpl;
import org.sonar.plugins.ruby.simplecovrcov.SimpleCovRcovSensor;

/**
 * This class is the entry point for all extensions
 */
@Properties({})
public final class RubyPlugin extends SonarPlugin
{
  public static final String SIMPLECOVRCOV_REPORT_PATH_PROPERTY  = "sonar.simplecovrcov.reportPath";
  public static final String METRICFU_REPORT_PATH_PROPERTY       = "sonar.metricfu.reportPath";
  public static final String METRICFU_COMPLEXITY_METRIC_PROPERTY = "sonar.metricfu.complexityMetric";

  public List<Object> getExtensions()
  {
    List<Object> extensions = new ArrayList<Object>();
    extensions.add(Ruby.class);
    extensions.add(SimpleCovRcovSensor.class);
    extensions.add(SimpleCovRcovJsonParserImpl.class);
    extensions.add(MetricfuComplexityYamlParserImpl.class);
    //extensions.add(RubySourceCodeColorizer.class);
    extensions.add(RubySensor.class);
    extensions.add(MetricfuComplexitySensor.class);

    // Profiles
    extensions.add(SonarWayProfile.class);

    PropertyDefinition metricfuReportPath = PropertyDefinition.builder(METRICFU_REPORT_PATH_PROPERTY)
        .category(CoreProperties.CATEGORY_CODE_COVERAGE)
        .subCategory("Ruby Coverage")
        .name("MetricFu Report path")
        .description("Path (absolute or relative) to MetricFu yml report file.")
        .defaultValue("tmp/metric_fu/report.yml")
        .onQualifiers(Qualifiers.PROJECT)
        .build();
    extensions.add(metricfuReportPath);

    PropertyDefinition simplecovrcovReportPath = PropertyDefinition.builder(SIMPLECOVRCOV_REPORT_PATH_PROPERTY)
        .category(CoreProperties.CATEGORY_CODE_COVERAGE)
        .subCategory("Ruby Coverage")
        .name("SimpleCovRcov Report path")
        .description("Path (absolute or relative) to SimpleCovRcov json report file.")
        .defaultValue("coverage/.resultset.json")
        .onQualifiers(Qualifiers.PROJECT)
        .build();
    extensions.add(simplecovrcovReportPath);

    List<String> options = Arrays.asList("Saikuro", "Cane");

    PropertyDefinition ComplexityMetric = PropertyDefinition.builder(METRICFU_COMPLEXITY_METRIC_PROPERTY)
        .category(CoreProperties.CATEGORY_CODE_COVERAGE)
        .subCategory("Ruby Coverage")
        .name("MetricFu Complexity Metric")
        .description("Type of complexity, Saikuro or Cane")
        .defaultValue("Saikuro")
        .onQualifiers(Qualifiers.PROJECT)
        .type(PropertyType.SINGLE_SELECT_LIST)
        .options(options)
        .build();
    extensions.add(ComplexityMetric);

    return extensions;
  }
}
