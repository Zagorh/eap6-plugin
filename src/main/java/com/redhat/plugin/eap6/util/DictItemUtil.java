/*
   Copyright 2018 Joao Rico

   This file is part of eap6 plugin.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/

package com.redhat.plugin.eap6.util;

import com.redhat.plugin.eap6.DictItem;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.everit.json.schema.ValidationException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class DictItemUtil {

    private DictItemUtil() {
    }

    public static List<DictItem> parseDictionaryFile(Reader rd) throws IOException, ValidationException {

        JSONObject dictionaryObj = new JSONObject(new JSONTokener(rd));

        JSONUtil.validateJSON(dictionaryObj);


        JSONArray itemsArray = dictionaryObj.getJSONArray("modules");

        List<DictItem> dictItemList = new ArrayList<DictItem>(itemsArray.length());

        for (Object item : itemsArray.toList()) {
            JSONObject itemObj = (JSONObject) item;

            DictItem dictItem = new DictItem();
            dictItem.setGroupId(itemObj.getString("groupId"));
            dictItem.setArtifactId(itemObj.getString("artifactId"));
            dictItem.setVersion(itemObj.getString("version"));
            dictItem.setModuleName(itemObj.getString("moduleName"));
            dictItem.setExport(itemObj.getString("export"));
            dictItem.setMetaInf(itemObj.getString("meta-inf"));

            dictItemList.add(dictItem);
        }

        return dictItemList;

    }

}
