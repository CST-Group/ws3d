/*****************************************************************************
 * Copyright 2007-2015 DCA-FEEC-UNICAMP
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *    Patricia Rocha de Toro, Elisa Calhau de Castro, Ricardo Ribeiro Gudwin
 *****************************************************************************/
package util;

/**
 *
 * @author rgudwin
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.Locale;
import java.util.logging.Logger;
 
/**
 * Simple library class for working with JNI (Java Native Interface)
 * 
 * @see http://frommyplayground.com/how-to-load-native-jni-library-from-jar
 *
 * @author Adam Heirnich <adam@adamh.cz>, http://www.adamh.cz
 */
public class NativeUtils {
    
    static Logger log = Logger.getLogger(NativeUtils.class.getCanonicalName());
 
    /**
     * Private constructor - this class will never be instanced
     */
    private NativeUtils() {
    }
    
    public static void setLibraryPath(String path) {
        Field fieldSysPath=null;
        System.setProperty( "java.library.path", path );
        try {
        fieldSysPath = ClassLoader.class.getDeclaredField( "sys_paths" );
        } catch (Exception e) {
            log.severe("Exceção: "+e);
        }
        fieldSysPath.setAccessible( true );
        try {
        fieldSysPath.set( null, null );
        } catch (Exception e) {
            log.severe("Exceção: "+e);
        }
    }
 
    public static void prepareNativeLibs() {
        log.info("OS:"+System.getProperties().getProperty("os.name")+" Architecture:"+System.getProperties().getProperty("os.arch"));
            
	    String osName = System.getProperty("os.name").toLowerCase(Locale.ENGLISH);
            String osArch = System.getProperty("os.arch").toLowerCase(Locale.ENGLISH);
				
            try {
 	      if(osName.contains("win")){
                  if(osArch.contains("64")) {
                    log.info("Windows 64 bits");
                    NativeUtils.loadFileFromJar("/windows/jinput-dx8_64.dll");
                    NativeUtils.loadFileFromJar("/windows/jinput-raw_64.dll");
                    NativeUtils.loadFileFromJar("/windows/lwjgl64.dll");
                    NativeUtils.loadFileFromJar("/windows/OpenAL64.dll");
                  }
                  else {
                    log.info("Windows 32 bits");
                    NativeUtils.loadFileFromJar("/windows/jinput-dx8.dll");
                    NativeUtils.loadFileFromJar("/windows/jinput-raw.dll");
                    NativeUtils.loadFileFromJar("/windows/lwjgl.dll");
                    NativeUtils.loadFileFromJar("/windows/OpenAL32.dll");
                  }   
              } else if(osName.contains("mac")){
		    log.info("MacOSX");
                    NativeUtils.loadFileFromJar("/macosx/libjinput-osx.jnilib");
                    NativeUtils.loadFileFromJar("/macosx/liblwjgl.jnilib");
                    NativeUtils.loadFileFromJar("/macosx/openal.dylib");
              } else if(osName.contains("nix") || osName.contains("nux")){
                  if(osArch.contains("64")) {
                      log.info("Linux 64 bits");
                      NativeUtils.loadFileFromJar("/linux/libjinput-linux64.so");
                      NativeUtils.loadFileFromJar("/linux/liblwjgl64.so");
                      NativeUtils.loadFileFromJar("/linux/libopenal64.so");
                  }
                 else {
                      log.info("Linux 32 bits");
                      NativeUtils.loadFileFromJar("/linux/libjinput-linux.so");
                      NativeUtils.loadFileFromJar("/linux/liblwjgl.so");
                      NativeUtils.loadFileFromJar("/linux/libopenal.so");
                }
              } else{
		//Unable to identify
		throw new IllegalStateException("Unable to determine what the operating system is, cannot automatically load native libraries");
	      }
            } catch (Exception e) {
		log.severe("Unable to load native libraries. They must be set manually with '-Djava.library.path'"+e);
	        //We failed. We shouldn't kill the application however, linking *may* have succeeded because of user manually setting location
            }
    }
    
    
    public static void loadFileFromJar(String path) throws IOException {
 
        if (!path.startsWith("/")) {
            throw new IllegalArgumentException("The path to be absolute (start with '/').");
        }
 
        // Obtain filename from path
        String[] parts = path.split("/");
        String filename = (parts.length > 1) ? parts[parts.length - 1] : null;
 
        // Split filename to prexif and suffix (extension)
        String prefix = "";
        String suffix = null;
        if (filename != null) {
            parts = filename.split("\\.", 2);
            prefix = parts[0];
            suffix = (parts.length > 1) ? "."+parts[parts.length - 1] : null; // Thanks, davs! :-)
        }
 
        // Check if the filename is okay
        if (filename == null || prefix.length() < 3) {
            throw new IllegalArgumentException("The filename has to be at least 3 characters long.");
        }
 
        log.info("Loading File from JAR: "+prefix+suffix);
        // Prepare temporary file
        //File temp = File.createTempFile(prefix, suffix);
        File temp = new File(prefix+suffix);
        temp.deleteOnExit();
 
//        if (!temp.exists()) {
//            throw new FileNotFoundException("File " + temp.getAbsolutePath() + " does not exist.");
//        }
 
        // Prepare buffer for data copying
        byte[] buffer = new byte[1024];
        int readBytes;
 
        // Open and check input stream
        InputStream is = NativeUtils.class.getResourceAsStream(path);
        if (is == null) {
            throw new FileNotFoundException("File " + path + " was not found inside JAR.");
        }
 
        // Open output stream and copy data between source file in JAR and the temporary file
        OutputStream os = new FileOutputStream(temp);
        try {
            while ((readBytes = is.read(buffer)) != -1) {
                os.write(buffer, 0, readBytes);
            }
        } finally {
            // If read/write fails, close streams safely before throwing an exception
            os.close();
            is.close();
        }
    }
    
    /**
     * Loads library from current JAR archive
     * 
     * The file from JAR is copied into system temporary directory and then loaded. The temporary file is deleted after exiting.
     * Method uses String as filename because the pathname is "abstract", not system-dependent.
     * 
     * @param filename The filename inside JAR as absolute path (beginning with '/'), e.g. /package/File.ext
     * @throws IOException If temporary file creation or read/write operation fails
     * @throws IllegalArgumentException If source file (param path) does not exist
     * @throws IllegalArgumentException If the path is not absolute or if the filename is shorter than three characters (restriction of {@see File#createTempFile(java.lang.String, java.lang.String)}).
     */
    public static void loadLibraryFromJar(String path) throws IOException {
 
        if (!path.startsWith("/")) {
            throw new IllegalArgumentException("The path to be absolute (start with '/').");
        }
 
        // Obtain filename from path
        String[] parts = path.split("/");
        String filename = (parts.length > 1) ? parts[parts.length - 1] : null;
 
        // Split filename to prexif and suffix (extension)
        String prefix = "";
        String suffix = null;
        if (filename != null) {
            parts = filename.split("\\.", 2);
            prefix = parts[0];
            suffix = (parts.length > 1) ? "."+parts[parts.length - 1] : null; // Thanks, davs! :-)
        }
 
        // Check if the filename is okay
        if (filename == null || prefix.length() < 3) {
            throw new IllegalArgumentException("The filename has to be at least 3 characters long.");
        }
 
        log.info("Loading Library: "+prefix+suffix);
        // Prepare temporary file
        //File temp = File.createTempFile(prefix, suffix);
        File temp = new File(prefix+suffix);
        temp.deleteOnExit();
 
//        if (!temp.exists()) {
//            throw new FileNotFoundException("File " + temp.getAbsolutePath() + " does not exist.");
//        }
 
        // Prepare buffer for data copying
        byte[] buffer = new byte[1024];
        int readBytes;
 
        // Open and check input stream
        InputStream is = NativeUtils.class.getResourceAsStream(path);
        if (is == null) {
            throw new FileNotFoundException("File " + path + " was not found inside JAR.");
        }
 
        // Open output stream and copy data between source file in JAR and the temporary file
        OutputStream os = new FileOutputStream(temp);
        try {
            while ((readBytes = is.read(buffer)) != -1) {
                os.write(buffer, 0, readBytes);
            }
        } finally {
            // If read/write fails, close streams safely before throwing an exception
            os.close();
            is.close();
        }
 
        // Finally, load the library
        System.load(temp.getAbsolutePath());
        
//        // Starts contributed code from Lofi to deal with files on the temporary folder in Windows
//        final String libraryPrefix = prefix;
//        final String lockSuffix = ".lock";
// 
//        // create lock file
//        final File lock = new File( temp.getAbsolutePath() + lockSuffix);
//        lock.createNewFile();
//        lock.deleteOnExit();
// 
//        // file filter for library file (without .lock files)
//        FileFilter tmpDirFilter =
//          new FileFilter()
//          {
//            public boolean accept(File pathname)
//            {
//              return pathname.getName().startsWith( libraryPrefix) && pathname.getName().endsWith( lockSuffix);
//            }
//          };
// 
//        // get all library files from temp folder  
//        String tmpDirName = System.getProperty("java.io.tmpdir");
//        File tmpDir = new File(tmpDirName);
//        File[] tmpFiles = tmpDir.listFiles(tmpDirFilter);
// 
//        // delete all files which don't have n accompanying lock file
//        for (int i = 0; i < tmpFiles.length; i++)
//        {
//          // Create a file to represent the lock and test.
//          File lockFile = new File( tmpFiles[i].getAbsolutePath() + lockSuffix);
//          if (!lockFile.exists())
//          {
//            System.out.println( "deleting: " + tmpFiles[i].getAbsolutePath());
//            tmpFiles[i].delete();
//          }
//        }
    }
}