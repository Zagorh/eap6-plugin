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
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class DictItemUtil {

    private DictItemUtil() {
    }

    public static List<DictItem> parseDictionaryFile(Reader rd) {
        JSONParser parser = new JSONParser();
        List<DictItem> dictItemList = new ArrayList<DictItem>();

        try {
            JSONObject dictionaryObj = (JSONObject) parser.parse(rd);

            JSONArray itemsArray = (JSONArray) dictionaryObj.get("items");

            for (Object item : itemsArray) {
                JSONObject itemObj = (JSONObject) item;

                DictItem dictItem = new DictItem();
                dictItem.setGroupId((String) itemObj.get("groupId"));
                dictItem.setArtifactId((String) itemObj.get("artifactId"));
                dictItem.setVersion((String) itemObj.get("version"));
                dictItem.setModuleName((String) itemObj.get("moduleName"));

                dictItemList.add(dictItem);
            }

        } catch (IOException e) {
            throw new IllegalArgumentException("Error reading the dictionary file", e);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Error parsing the dictionary file.", e);
        }

        return dictItemList;
    }

}
