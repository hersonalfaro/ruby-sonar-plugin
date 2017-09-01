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
package org.sonar.plugins.ruby.simplecovrcov;
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
import java.io.IOException;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.measures.CoverageMeasuresBuilder;
import org.sonar.plugins.ruby.simplecovrcov.SimpleCovRcovJsonParser;

import com.google.common.collect.Maps;

public class SimpleCovRcovJsonParserImpl implements SimpleCovRcovJsonParser
{
    private static final Logger LOG = LoggerFactory.getLogger(SimpleCovRcovJsonParserImpl.class);

    public Map<String, CoverageMeasuresBuilder> parse(File file) throws IOException
    {
        Map<String, CoverageMeasuresBuilder> coveredFiles = Maps.newHashMap();

        File fileToFindCoverage = file;

        String fileString = FileUtils.readFileToString(fileToFindCoverage, "UTF-8");

        JSONObject resultJsonObject = (JSONObject) JSONValue.parse(fileString);
        JSONObject coverageJsonObj = (JSONObject) ((JSONObject) resultJsonObject.get("RSpec")).get("coverage");

        // for each file in the coverage report
        for (int j = 0; j < coverageJsonObj.keySet().size(); j++)
        {
            CoverageMeasuresBuilder fileCoverage = CoverageMeasuresBuilder.create();

            String filePath = coverageJsonObj.keySet().toArray()[j].toString();
        	LOG.debug("filePath " + filePath);

            JSONArray coverageArray = (JSONArray) coverageJsonObj.get(coverageJsonObj.keySet().toArray()[j]);

            // for each line in the coverage array
            for (int i = 0; i < coverageArray.size(); i++)
            {
                Long line = (Long) coverageArray.toArray()[i];
                Integer intLine = 0;
                int lineNumber = i + 1;
                if (line != null)
                {
                    intLine = line.intValue();
                    fileCoverage.setHits(lineNumber, intLine);
                }
            }
            LOG.info("FILE COVERAGE = " + fileCoverage.getCoveredLines());
            coveredFiles.put(filePath, fileCoverage);
        }
        return coveredFiles;
    }
}
