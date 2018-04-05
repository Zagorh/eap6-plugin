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

package com.redhat.plugin.eap6;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

@Mojo(name = "build-dictionary",
        requiresDependencyResolution = ResolutionScope.COMPILE,
        defaultPhase = LifecyclePhase.VALIDATE,
        threadSafe = true)
public class EAP6DictionaryMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project.basedir}/dict.json", required = true)
    protected File workDirectory;

    @Parameter(defaultValue = "${env.JBOSS_HOME}/modules", required = true)
    protected File modulesDirectory;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        Collection<File> jars = FileUtils.listFiles(modulesDirectory, new String[]{"jar"}, true);
        getLog().debug(jars.size() + " jars found.");

        JSONObject jsonObject = new JSONObject();
        JSONArray items = new JSONArray();
        jsonObject.put("items", items);

        for (File jar : jars) {
            JarFile jarFile = null;
            try {
                jarFile = new JarFile(jar);

                Properties props = getPropertiesFromJar(jarFile);
                if (props == null) {
                    continue;
                }

                File moduleXml = getModuleXmlFile(jar);

                if (moduleXml == null || !moduleXml.exists()) {
                    getLog().warn("No moduleXml in folder: " + jar.getParentFile().getAbsolutePath());
                    continue;
                }

                Map<String, String> moduleProps = getModuleNameFromModuleXml(moduleXml);

                if (moduleProps == null || moduleProps.isEmpty()) {
                    getLog().warn("No moduleName found in xml: " + moduleXml.getAbsolutePath());
                }

                JSONObject item = new JSONObject();

                item.put("groupId", props.getProperty("groupId"));
                item.put("artifactId", props.getProperty("artifactId"));
                item.put("moduleName", moduleProps.get("name"));

                String slot = moduleProps.get("slot");
                if (slot != null) {
                    item.put("slot", slot);
                    item.put("needsPomSlot", true);
                }

                items.put(item);

            } catch (IOException e) {
                getLog().warn("Could not read the jar file: " + jar.getAbsolutePath());
                if (getLog().isDebugEnabled()) {
                    getLog().debug(e.getMessage(), e);
                }
            }
        }

        try {
            printJsonObject(jsonObject);
        } catch (Exception e) {
            throw new MojoFailureException("Could not write to file.", e);
        }

    }

    private Properties getPropertiesFromJar(JarFile jarFile) throws IOException {

        JarEntry pomPropertiesEntry = null;
        Enumeration<JarEntry> entries = jarFile.entries();
        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();

            String filename = entry.getName();
            if (filename.startsWith("META-INF") && filename.endsWith("pom.properties")) {
                pomPropertiesEntry = entry;
                break;
            }
        }

        if (pomPropertiesEntry == null) {
            getLog().warn(jarFile.getName() + " does not contain a pom.properties file");
            return null;
        }

        InputStream is = jarFile.getInputStream(pomPropertiesEntry);
        try {
            Properties props = new Properties();
            props.load(is);
            return props;
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    private File getModuleXmlFile(File jarFile) {
        File currentDirectory = jarFile;
        File moduleXml = null;

        while(currentDirectory.getParent() != null) {
            currentDirectory = currentDirectory.getParentFile();

            moduleXml = new File(currentDirectory, "module.xml");

            if (moduleXml.exists()) {
                break;
            }
        }

        return moduleXml;
    }

    private Map<String, String> getModuleNameFromModuleXml(File moduleXml) {
        try {
            Map<String, String> result = new HashMap<String, String>();
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(false);

            Document module = factory.newDocumentBuilder().parse(moduleXml);

            result.put("name", module.getDocumentElement().getAttribute("name"));

            String slot = module.getDocumentElement().getAttribute("slot");
            if (slot != null && !slot.isEmpty() && !slot.equals("main")) {
                result.put("slot", slot);
            }

            return result;
        } catch (SAXException e) {
            getLog().warn("Problem parsing the moduleXml file: " + moduleXml.getAbsolutePath(), e);
        } catch (ParserConfigurationException e) {
            getLog().warn("Problem parsing the moduleXml file: " + moduleXml.getAbsolutePath(), e);
        } catch (IOException e) {
            getLog().warn("Problem parsing the moduleXml file: " + moduleXml.getAbsolutePath(), e);
        }

        return null;
    }

    private void printJsonObject(JSONObject jsonObject) throws IOException {
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(workDirectory);
            fileWriter.write(jsonObject.toString(4));
        } finally {
            if (fileWriter != null) {
                fileWriter.flush();
                fileWriter.close();
            }
        }

    }


}
