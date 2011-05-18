package org.ilrt.mca.plugins;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @goal hiya
 */
public class FindDataFilesMojo extends AbstractMojo {

    /**
     * The directory for the generated WAR.
     *
     * @parameter default-value="${project.build.directory}/classes/"
     * @required
     */
    private File outputDirectory;

    /**
     * The base directory for the local data.
     *
     * @parameter default-value="${basedir}/src/main/resources/"
     * @required
     */
    private File resourcesDir;

    /**
     * The base directory for the registry data.
     *
     * @parameter default-value="${basedir}/src/main/resources/data/registry/"
     * @required
     */
    private File registryDir;

    /**
     * The base directory for the registry data.
     *
     * @parameter default-value="${basedir}/src/main/resources/data/graph/"
     * @required
     */
    private File graphDir;

    public void execute() throws MojoExecutionException {

        List<String> registryFiles = new ArrayList<String>();
        List<String> graphFiles = new ArrayList<String>();

        processDirectory(registryDir, registryFiles);
        processDirectory(graphDir, graphFiles);


        File f = outputDirectory;

        File output = new File(f, "data.txt");

        FileWriter writer = null;
        try {
            writer = new FileWriter(output);

            writer.write("# This is an automatically generated file");

            for (String val : registryFiles) {
                writer.write("registry:" + val + "\n");
            }

            for (String val : graphFiles) {
                writer.write("graph:" + val + "\n");
            }

        } catch (IOException e) {
            throw new MojoExecutionException("Error creating file " + output, e);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


    }

    private void processDirectory(File f, List list) {
        if (f.exists()) {
            if (f.isDirectory()) {
                File[] files = f.listFiles();
                for (int i = 0; i < files.length; i++) {
                    processDirectory(files[i], list);
                }
            } else {
                list.add(f.getAbsolutePath().substring(resourcesDir.getAbsolutePath().length()));
            }
        }
    }



}
