package com.myzip;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.attribute.FileTime;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class ZipUtil
{
    public static void main(String[] args)
    {

        /**
         При считывании записейй zip'а
         может вылетать исключение из-за проблеем с кодировкой.
         при архивации WinRar'ом в zip с кириллцей возникало исключение  java.lang.IllegalArgumentException: MALFORMED

         решение
         Charset cp866 = Charset.forName("CP866");
         ZipFile file = new ZipFile("архив.zip", cp866);
         */

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

            byte[] buffer = new byte[8192];

            doZip(stream, file, buffer);

        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }


        System.out.println("Done.");
    }


    /**
     * Создает ZIP-архив
     *
     * @param stream
     * @param dir
     * @param buffer
     * @throws IOException
     */
    public static void doZip(ZipOutputStream stream, File dir, byte[] buffer) throws IOException
    {
        for (File f : dir.listFiles())
        {
            if (f.isDirectory())
            {
                //для пустых папок:
                ZipEntry entry = new ZipEntry(f.getPath() + "\\");  // тут проблемма, не создаются директории
                entry.setLastModifiedTime(FileTime.fromMillis(f.lastModified()));
                stream.putNextEntry(entry);

                doZip(stream, f, buffer); //рекурсивный вызов
                stream.closeEntry();
            }
            else
            {
                ZipEntry entry = new ZipEntry(f.getPath());
                entry.setLastModifiedTime(FileTime.fromMillis(f.lastModified()));
                stream.putNextEntry(entry);

                int length;
                FileInputStream fin = new FileInputStream(f);
                while ((length = fin.read(buffer)) != -1)
                    stream.write(buffer, 0, length);
                stream.closeEntry();
                fin.close();
            }
        }


    }


    /**
     * Показывает содерживое ZIP-архива
     * @param file
     */
    public static void showZip(ZipFile file)
    {
        System.out.println("Archive name: " + file.getName());
        System.out.println("Entries: " + file.size());

        Enumeration<? extends ZipEntry> nm = file.entries();
        System.out.println();

        while (nm.hasMoreElements())
        {
            ZipEntry entry = nm.nextElement();
            if (entry.isDirectory()) System.out.println("<DIR>  " + entry.getName());
            else System.out.println("       " + entry.getName());
        }

    }

    public static void unZip()
    {
    }


}
