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

import com.redhat.plugin.eap6.data.DictItem;
import com.redhat.plugin.eap6.data.DictModuleInfo;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.artifact.Artifact;
import org.everit.json.schema.ValidationException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DictItemUtil {

    private DictItemUtil() {
    }

    public static DictModuleInfo fetchDictModuleInfo(JSONObject jsonObject) {
        DictModuleInfo dictModuleInfo = null;
        JSONObject moduleInfoJson = jsonObject.getJSONObject("moduleInfo");

        if (moduleInfoJson != null) {
            dictModuleInfo = new DictModuleInfo();
            dictModuleInfo.setName(moduleInfoJson.getString("name"));
            dictModuleInfo.setNeedsPomSlot(moduleInfoJson.optBoolean("needsPomSlot"));
            dictModuleInfo.setSlot(moduleInfoJson.optString("slot", null));
        }

        return dictModuleInfo;
    }

    public static List<DictItem> fetchDictionaryItems(JSONObject jsonObject) {

        JSONArray itemsArray = jsonObject.getJSONArray("modules");

        List<DictItem> dictItemList = new ArrayList<DictItem>(itemsArray.length());

        for (Object item : itemsArray.toList()) {
            JSONObject itemObj = (JSONObject) item;

            DictItem dictItem = new DictItem();
            dictItem.setGroupId(itemObj.getString("groupId"));
            dictItem.setArtifactId(itemObj.getString("artifactId"));
            dictItem.setModuleName(itemObj.getString("moduleName"));
            dictItem.setVersion(itemObj.optString("version", null));
            dictItem.setExport(itemObj.optString("export", null));
            dictItem.setMetaInf(itemObj.optString("meta-inf", null));
            dictItem.setNeedsPomSlot(itemObj.optBoolean("needsPomSlot"));
            dictItem.setSlot(itemObj.optString("slot", null));

            dictItemList.add(dictItem);
        }

        return dictItemList;

    }

    public static Map<String, Map<String, String>> getModuleEntriesAttributesForDeploymentStructure(Map<Artifact, DictItem> items) {
        Map<String, Map<String, String>> result = new LinkedHashMap<String, Map<String, String>>();
        for(Map.Entry<Artifact, DictItem> entry : items.entrySet()) {
            result.put(entry.getValue().getModuleName(), getModuleEntryAttributesForDeploymentStructure(entry.getValue(), entry.getKey()));
        }
        return result;
    }

    public static Map<String, String> getModuleEntryAttributesForDeploymentStructure(DictItem item, Artifact artifact) {
        Map<String, String> props = new LinkedHashMap<String, String>();
        props.put("name", item.getModuleName());

        if (item.isNeedsPomSlot()) {
            props.put("slot", (StringUtils.isNotEmpty(item.getSlot()) ? item.getSlot() : artifact.getVersion()));
        }

        if (StringUtils.isNotEmpty(item.getExport())) {
            props.put("export", item.getExport());
        }

        if (StringUtils.isNotEmpty(item.getMetaInf())) {
            props.put("meta-inf", item.getMetaInf());
        }

        return props;
    }

    public static Map<String, String> getDependencyEntryAttributesForModule(DictItem item, Artifact artifact) {
        Map<String, String> props = new LinkedHashMap<String, String>();
        props.put("name", item.getModuleName());

        if (item.isNeedsPomSlot()) {
            props.put("slot", (StringUtils.isNotEmpty(item.getSlot()) ? item.getSlot() : artifact.getVersion()));
        }

        if (StringUtils.isNotEmpty(item.getExport())) {
            props.put("export", item.getExport());
        }

        return props;
    }

}
