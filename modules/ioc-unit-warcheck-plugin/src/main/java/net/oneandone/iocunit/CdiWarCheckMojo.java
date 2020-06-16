package net.oneandone.iocunit;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

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

/**
 * Goal which checks integrity of a war
 *
 * @goal check
 * 
 * @phase package
 */
@Mojo(name="check", defaultPhase = LifecyclePhase.PRE_INTEGRATION_TEST)
public class CdiWarCheckMojo
    extends AbstractMojo
{

    @Parameter(property = "project", required = true, readonly = true)
    MavenProject project;

    @Parameter(property = "project.build.directory", required = true, readonly = true)
    String buildDirectory;


    public static void unzip(final ZipFile zipfile, final File directory)
            throws IOException {

        final Enumeration<? extends ZipEntry> entries = zipfile.entries();
        while (entries.hasMoreElements()) {
            final ZipEntry entry = entries.nextElement();
            final File file = file(directory, entry);
            if (entry.isDirectory()) {
                continue;
            }
            final InputStream input = zipfile.getInputStream(entry);
            try {
                // copy bytes from input to file
            } finally {
                input.close();
            }
        }
    }

    protected static File file(final File root, final ZipEntry entry)
            throws IOException {

        final File file = new File(root, entry.getName());

        File parent = file;
        if (!entry.isDirectory()) {
            final String name = entry.getName();
            final int index = name.lastIndexOf('/');
            if (index != -1) {
                parent = new File(root, name.substring(0, index));
            }
        }
        if (parent != null && !parent.isDirectory() && !parent.mkdirs()) {
            throw new IOException(
                    "failed to create a directory: " + parent.getPath());
        }

        return file;
    }

    public void execute()
        throws MojoExecutionException
    {
        try {
            File dir = new File(buildDirectory);
            for (File f : dir.listFiles()) {
                if(f.isFile() && f.getName().endsWith(".war")) {
                    ZipFile zf = new ZipFile(f);
                    Enumeration<? extends ZipEntry> entries = zf.entries();
                    while (entries.hasMoreElements()) {
                        ZipEntry ze = entries.nextElement();
                        // analyzeZipEntry(zf, ze);
                    }

                }
            }
        } catch(Exception e) {
            throw new MojoExecutionException("CdiWarChecker",e);
        }


    }
}
