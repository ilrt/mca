package org.ilrt.mca.plugins;

/*
 * Copyright (c) 2009, University of Bristol
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1) Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2) Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3) Neither the name of the University of Bristol nor the names of its
 *    contributors may be used to endorse or promote products derived from this
 *    software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Finds all the registry and local data files to create a manifest of their
 * names and locations. The system can the find and load the files via a
 * manifest into the database.
 *
 * @goal find-data
 */
public class FindDataFilesMojo extends AbstractMojo {

    /**
     * The directory for the generated classes
     *
     * @parameter default-value="${project.build.directory}/classes/".
     * @required
     */
    private File outputDirectory = null;

    /**
     * The resources directory (base directory for the local data).
     *
     * @parameter default-value="${basedir}/src/main/resources/".
     * @required
     */
    private File resourcesDir = null;

    /**
     * The base directory for the registry data.
     *
     * @parameter default-value="${basedir}/src/main/resources/data/registry/"
     * @required
     */
    private File registryDir = null;

    /**
     * The base directory for other data to be placed in named graphs.
     *
     * @parameter default-value="${basedir}/src/main/resources/data/graph/"
     * @required
     */
    private File graphDir = null;

    /**
     * The name of the data manifest file
     * @parameter default-value="data-manifest.txt"
     */
    private String manifestFileName = null;

    public FindDataFilesMojo() {}

    public void execute() throws MojoExecutionException {

        // hold the location of the files
        List<String> registryFiles = new ArrayList<String>();
        List<String> graphFiles = new ArrayList<String>();

        // process the registry files
        processDirectory(registryDir, registryFiles);

        // process the local data files
        processDirectory(graphDir, graphFiles);


        File f = outputDirectory;

        File output = new File(f, manifestFileName);

        FileWriter writer = null;
        try {
            writer = new FileWriter(output);

            writer.write("# This is an automatically generated file\n");

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

    /**
     * Search directories and sub directories for data files.
     *
     * @param f a file or a directory.
     * @param list list of found data files.
     */
    private void processDirectory(File f, List<String> list) {
        if (f.exists()) {
            if (f.isDirectory()) {  // if its a directory, look for its contents
                File[] files = f.listFiles();
                for (File file : files) {
                    processDirectory(file, list); // recursive call to deal with contents
                }
            } else { // its a file so add its location to the list
                list.add(f.getAbsolutePath().substring(resourcesDir.getAbsolutePath().length()));
            }
        }
    }

}
