package com.myzip;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;


/**
 * в винде проверить
 *
 * При считывании записей zip-архива, созданного WinRar-ом с кириллицей,
 * может вылетать исключение из-за проблем с кодировкой:
 * java.lang.IllegalArgumentException: MALFORMED
 * <p>
 * решение:
 * Charset cp866 = Charset.forName("CP866");
 * ZipFile file = new ZipFile("archive.zip", cp866);
 */
public class ZipUtil
{
    public static final String SP = File.separator;


    public static void main(String[] args) throws IOException
    {
        int n = 1;

        if (n == 1)
        {
            ZipOperations.showZip(new ZipFile("te" + SP + "archive.zip"));
            System.exit(0);
        }

        if (n == 2)
        {
            ZipFile archive = new ZipFile(new File("te" + SP + "archive.zip"));
            File out = new File("te");
            ZipOperations.unZip(archive, out);
            archive.close();
            System.exit(0);
        }


        File fin = new File("input" + SP + "forZip");
        File fout = new File("out" + SP + "archive.zip");

        try (ZipOutputStream stream = new ZipOutputStream(new FileOutputStream(fout)))
        {
            System.out.println(" input: " + fin.getAbsolutePath());
            System.out.println("output: " + fout.getAbsolutePath());
            System.out.println("\nList of files and folders for archiving:");
            for (File f : fin.listFiles())
            {
                if (f.isDirectory()) System.out.println("<DIR>  " + f.getName());
                else System.out.println("       " + f.getName());
            }

            ZipOperations.doZip(stream, fin);
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }


        System.out.println("Done.");
    }





}
