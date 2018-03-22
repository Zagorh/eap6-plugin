/*
   Copyright 2018 Joao Rico
   Copyright 2013 Red Hat, Inc. and/or its affiliates.

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
package com.redhat.plugin.eap6.data;

import com.redhat.plugin.eap6.util.DictItemUtil;
import com.redhat.plugin.eap6.util.JSONUtil;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Dictionary {

    DictModuleInfo moduleInfo;
    Map<String, JSONObject> moduleGroups = new HashMap<String, JSONObject>();
    Map<String, DictItem> dictionaries = new LinkedHashMap<String, DictItem>();

    public DictModuleInfo getModuleInfo() {
        return moduleInfo;
    }

    public void setModuleInfo(DictModuleInfo moduleInfo) {
        this.moduleInfo = moduleInfo;
    }

    public Map<String, DictItem> getDictionaries() {
        return dictionaries;
    }

    public void setDictionaries(Map<String, DictItem> dictionaries) {
        this.dictionaries = dictionaries;
    }

    public void addDictionary(Collection<DictItem> dictList) {
        for (DictItem item : dictList) {
            dictionaries.put(dictionaryKey(item), item);
        }
    }

    public void addDictionary(File f) throws IOException {
        FileReader reader = new FileReader(f);
        JSONObject jsonObject = transformFileToJsonObject(reader);

        setModuleInfo(DictItemUtil.fetchDictModuleInfo(jsonObject));
        addDictionary(DictItemUtil.fetchDictionaryItems(jsonObject));
    }

    public void addDictionary(InputStream stream) throws IOException {
        JSONObject jsonObject = transformFileToJsonObject(new InputStreamReader(stream));

        setModuleInfo(DictItemUtil.fetchDictModuleInfo(jsonObject));
        addDictionary(DictItemUtil.fetchDictionaryItems(jsonObject));
    }

    private JSONObject transformFileToJsonObject(Reader rd) throws IOException {
        JSONObject dictionaryObj = new JSONObject(new JSONTokener(rd));

        JSONUtil.validateJSON(dictionaryObj);

        return dictionaryObj;
    }

    public DictItem find(String groupId, String artifactId, String version) {
        DictItem item = dictionaries.get(dictionaryKey(groupId, artifactId, version));
        if (item == null) {
            item = dictionaries.get(dictionaryKey(groupId, artifactId, "*"));
        }
        return item;
    }

    private String dictionaryKey(DictItem dictItem) {
        return dictionaryKey(dictItem.getGroupId(), dictItem.getArtifactId(), dictItem.getVersion());
    }

    private String dictionaryKey(String groupId, String artifactId, String version) {
        return groupId + "|" + artifactId + "|" + (version != null ? version : "");
    }

}
