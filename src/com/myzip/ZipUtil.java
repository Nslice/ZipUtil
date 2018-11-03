package com.myzip;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipUtil
{
    public static void main(String[] args)
    {
        System.out.println("ZipUtil.main");
        try (ZipOutputStream stream = new ZipOutputStream(new FileOutputStream(args[1])))
        {
            File file = new File(args[0]);
            System.out.println("absolute path: " + file.getAbsolutePath());
            System.out.println("         path: " + file.getPath());
            System.out.println("\nList of files and folders for archiving:");
            for (File f : file.listFiles())
            {
                if (f.isDirectory()) System.out.println("<DIR>  " + f.getName());
                else System.out.println("       " + f.getName());
            }

            doZip(stream, file);

        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }


        System.out.println("Done.");
    }

    public static void doZip(ZipOutputStream stream, File dir) throws IOException
    {
        for (File f : dir.listFiles())
        {
            if (f.isDirectory())
            {
                //для пустых папок:
                stream.putNextEntry(new ZipEntry(f.getPath() + "/"));
                doZip(stream, f);
                stream.closeEntry();
            }
            else
            {
                stream.putNextEntry(new ZipEntry(f.getPath()));
                int code;
                FileInputStream fin = new FileInputStream(f);
                while ((code = fin.read()) != -1)
                    stream.write(code);
                stream.closeEntry();
                fin.close();
            }
        }


    }




}
