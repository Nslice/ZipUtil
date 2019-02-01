package com.myzip;

import java.io.*;
import java.nio.file.attribute.FileTime;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
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
        int n = 0;

        if (n == 1)
        {
            showZip(new ZipFile("te" + SP + "archive.zip"));
            System.exit(0);
        }

        if (n == 2)
        {
            ZipFile archive = new ZipFile(new File("te" + SP + "archive.zip"));
            File out = new File("te");
            unZip(archive, out);
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

            doZip(stream, fin);
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }


        System.out.println("Done.");
    }


    public static void doZip(ZipOutputStream stream, File path) throws IOException, ZipException
    {
        byte[] buffer = new byte[8192];
        doZip(stream, path, buffer);
    }

    private static void doZip(ZipOutputStream stream, File path, byte[] buffer) throws IOException, ZipException
    {
        /*
          entry.setLastModifiedTime() устанавливает время изменения как было.
          По умолчанию всем файлам в дату последнего изменения ставится
          дата создания.
         */
        for (File f : path.listFiles())
        {
            if (f.isDirectory())
            {
                ZipEntry entry = new ZipEntry(f.getPath() + SP);
                entry.setLastModifiedTime(FileTime.fromMillis(f.lastModified()));
                stream.putNextEntry(entry);

                doZip(stream, f, buffer);
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


    public static void showZip(ZipFile zip)
    {
        Enumeration<? extends ZipEntry> entries = zip.entries();

        SimpleDateFormat form = new SimpleDateFormat("yyyy.MM.dd - HH:mm");
        while (entries.hasMoreElements())
        {
            ZipEntry entry = entries.nextElement();
            Date date = new Date(entry.getLastModifiedTime().toMillis());
            System.out.print(form.format(date));
            System.out.println("\t" + entry.getName());
        }
    }


    public static void unZip(ZipFile zip, File outPath) throws IOException
    {
        Enumeration<? extends ZipEntry> entries = zip.entries();
        byte[] buffer = new byte[8192];

        while (entries.hasMoreElements())
        {
            ZipEntry entry = entries.nextElement();
            if (entry.isDirectory())
            {
                new File(outPath, entry.getName()).mkdirs();
            }
            else
            {
                File newFile = new File(outPath, entry.getName());
                new File(newFile.getParent()).mkdirs();//создаю все несуществующие папки
                System.out.println("file unzip : " + newFile.getAbsoluteFile());

                OutputStream out = new FileOutputStream(newFile);
                InputStream in = zip.getInputStream(entry);

                int length;
                while ((length = in.read(buffer)) != -1)
                    out.write(buffer, 0, length);

                out.close();
                in.close();
            }
        }
    }


}
