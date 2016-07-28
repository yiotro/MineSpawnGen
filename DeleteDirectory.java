import java.io.File;
import java.io.IOException;

public class DeleteDirectory {

    public static void deleteFolder(String folder) {
        File directory = new File(folder);

        //make sure directory exists
        if(!directory.exists()){
            System.out.println("Directory does not exist.");
        }else{
            try{
                delete(directory);
                YioSys.say("Deleted directory " + folder);
            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }

    public static void delete(File file)
            throws IOException{

        if(file.isDirectory()){

            //directory is empty, then delete it
            if(file.list().length==0){

                file.delete();
            }else{

                //list all the directory contents
                String files[] = file.list();

                for (String temp : files) {
                    //construct the file structure
                    File fileDelete = new File(file, temp);

                    //recursive delete
                    delete(fileDelete);
                }

                //check the directory again, if empty then delete it
                if(file.list().length==0){
                    file.delete();
                }
            }

        }else{
            //if file, then delete it
            file.delete();
        }
    }
}