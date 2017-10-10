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
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

//import org.apache.commons.io.FileUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.measures.CoverageMeasuresBuilder; 

//import com.google.common.collect.Maps;

public class SimpleCovRcovJsonParserImpl implements SimpleCovRcovJsonParser
{
    private static final Logger LOG = LoggerFactory.getLogger(SimpleCovRcovJsonParserImpl.class);

    public static String readFileToString(File path, String encoding) 
  		  throws IOException 
  		{
  	  return readFileToString(path, Charset.forName(encoding));
  	  
  		}
    public static String readFileToString(File path, Charset encoding) 
  		  throws IOException 
  		{
  		  byte[] encoded = Files.readAllBytes(Paths.get(path.toURI()));
  		  return new String(encoded, encoding);
  		}
    
    public Map<String, CoverageMeasuresBuilder> parse(File file) throws IOException
    {
        Map<String, CoverageMeasuresBuilder> coveredFiles = new HashMap<String, CoverageMeasuresBuilder>();

        File fileToFindCoverage = file;

        String fileString = readFileToString(fileToFindCoverage, "UTF-8");

        JSONObject resultJsonObject = (JSONObject) JSONValue.parse(fileString);
        
        
//        Iterator<String> keys = resultJsonObject.keySet().iterator();
//        // get some_name_i_wont_know in str_Name
//        String str_Name=keys.next(); 
//        // get the value i care about
//        JSONObject centralJsonObj = (JSONObject) ( resultJsonObject.get
//        
        
        JSONObject centralJsonObj = (resultJsonObject.containsKey("RSpec") ?(JSONObject) resultJsonObject.get("RSpec") : null);
        if(centralJsonObj == null) {
        	 
        	centralJsonObj = (resultJsonObject.containsKey("Unit Tests") ?(JSONObject)resultJsonObject.get("Unit Tests") : null);
            
        }
        
        if(centralJsonObj == null) {
        	// minitest doesn't comply with "RSpec" or "Unit Tests" labels
        	 Iterator<String> keys = resultJsonObject.keySet().iterator();

           String firstElement=keys.next(); 

            centralJsonObj = (JSONObject) ( resultJsonObject.get(firstElement));
        }
        
        
        
        
        JSONObject coverageJsonObj = (JSONObject) (centralJsonObj).get("coverage");

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
