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


public class ZipOperations
{
    public static final String SP = File.separator;
    public static final int BUFF_SIZE = 8192;


    public static void doZip(ZipOutputStream stream, File path) throws IOException, ZipException
    {
        byte[] buffer = new byte[BUFF_SIZE];
        doZip(stream, path, buffer);
    }

    private static void doZip(ZipOutputStream stream, File path, byte[] buffer) throws IOException, ZipException
    {
        /*
          entry.setLastModifiedTime() устанавливает время изменения как было.
          По умолчанию всем файлам в дату последнего изменения ставится
          дата создания архива.
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
            System.out.print(form.format(new Date(entry.getLastModifiedTime().toMillis())));
            System.out.println("\t" + entry.getName());
        }
    }


    public static void unZip(ZipFile zip, File outPath) throws IOException
    {
        Enumeration<? extends ZipEntry> entries = zip.entries();
        byte[] buffer = new byte[BUFF_SIZE];

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
