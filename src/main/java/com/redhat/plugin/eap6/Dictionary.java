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
package com.redhat.plugin.eap6;

import com.redhat.plugin.eap6.util.DictItemUtil;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Dictionary {

    Map<String, DictItem> dictionaries = new LinkedHashMap<String, DictItem>();

    public void addDictionary(Collection<DictItem> dictList) {
        for (DictItem item : dictList) {
            dictionaries.put(dictionaryKey(item), item);
        }
    }

    public void addDictionary(File f) throws IOException, ParseException {
        FileReader reader = new FileReader(f);
        addDictionary(DictItemUtil.parseDictionaryFile(reader));
    }

    public void addDictionary(InputStream stream) throws IOException, ParseException {
        addDictionary(DictItemUtil.parseDictionaryFile(new InputStreamReader(stream)));
    }

    public DictItem find(String groupId, String artifactId, String version) {
        return dictionaries.get(dictionaryKey(groupId, artifactId, version));
    }

    private String dictionaryKey(DictItem dictItem) {
        return dictionaryKey(dictItem.getGroupId(), dictItem.getArtifactId(), dictItem.getVersion());
    }

    private String dictionaryKey(String groupId, String artifactId, String version) {
        return groupId + "|" + artifactId + "|" + (version != null ? version : "");
    }

}
