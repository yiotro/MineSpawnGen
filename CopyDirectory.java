import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class CopyDirectory
{

    public static void copyWorldToSavesFolder() {
        File srcFolder = new File(System.getProperty("user.dir") + "\\worlds");
        File destFolder = new File(Parameters.FOLDER);

        //make sure source exists
        if(!srcFolder.exists()){

            YioSys.say("Can't copy folder because source directory " + srcFolder + " does not exist.");
            //just exit

        }else{

            try{
                copyFolder(srcFolder,destFolder);
                YioSys.say("Copied directory " + srcFolder + " to " + destFolder);
            }catch(IOException e){
                e.printStackTrace();
                //error, just exit
            }
        }
    }

    public static void copyFolder(File src, File dest)
            throws IOException{

        if(src.isDirectory()){

            //if directory not exists, create it
            if(!dest.exists()){
                dest.mkdir();
            }

            //list all the directory contents
            String files[] = src.list();

            for (String file : files) {
                //construct the src and dest file structure
                File srcFile = new File(src, file);
                File destFile = new File(dest, file);
                //recursive copy
                copyFolder(srcFile,destFile);
            }

        }else{
            //if file, then copy it
            //Use bytes stream to support all file types
            InputStream in = new FileInputStream(src);
            OutputStream out = new FileOutputStream(dest);

            byte[] buffer = new byte[1024];

            int length;
            //copy the file content in bytes
            while ((length = in.read(buffer)) > 0){
                out.write(buffer, 0, length);
            }

            in.close();
            out.close();
        }
    }
}