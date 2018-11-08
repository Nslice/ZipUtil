package com.myzip;

import java.io.*;
import java.nio.file.attribute.FileTime;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;


class test
{
    public static void main(String[] args) throws Exception
    {
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream("newArch.zip"));

        File file = new File("OLD\\", "input");

        doZip(file, out);

        out.close();
    }

    private static void doZip(File dir, ZipOutputStream out) throws IOException
    {
        for (File f : dir.listFiles())
        {
            if (f.isDirectory())
                doZip(f, out);
            else
            {
                out.putNextEntry(new ZipEntry(f.getPath()));
                write(new FileInputStream(f), out);
            }
        }
    }

    private static void write(InputStream in, OutputStream out) throws IOException
    {
        byte[] buffer = new byte[1024];
        int len;
        while ((len = in.read(buffer)) >= 0)
            out.write(buffer, 0, len);
        in.close();
    }
}


class UnzipUtil
{
    public static void main(String... args)
    {
        if (args.length < 1)
        {
            System.out.println("Usage: UnzipUtil [zipfile]");
            return;
        }

        File file = new File(args[0]);
        File outpath = new File(args[1]);
        outpath.mkdir();


        if (!file.exists() || !file.canRead())
        {
            System.out.println("File cannot be read");
            return;
        }

        try
        {
            ZipFile zip = new ZipFile(args[0]);
            Enumeration entries = zip.entries();

            while (entries.hasMoreElements())
            {
                ZipEntry entry = (ZipEntry) entries.nextElement();
                System.out.println(entry.getName());

                if (entry.isDirectory())
                {
                    System.out.println(file.getParent());
                    System.out.println(file.getName());
                    new File(file.getParent(), entry.getName()).mkdirs();
                }
                else
                {
                    System.out.println(file.getParent());
                    System.out.println(file.getName());
                    write(zip.getInputStream(entry),
                          new BufferedOutputStream(new FileOutputStream(
                                  new File(outpath.getParent(), entry.getName()))));
                }
            }

            zip.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private static void write(InputStream in, OutputStream out) throws IOException
    {
        byte[] buffer = new byte[1024];
        int len;
        while ((len = in.read(buffer)) >= 0)
            out.write(buffer, 0, len);
        out.close();
        in.close();
    }
}


public class ZipUtil
{


    /**
     * При считывании записей zip-архива, созданного WinRar-ом с кириллицей,
     * может вылетать исключение из-за проблем с кодировкой:
     * java.lang.IllegalArgumentException: MALFORMED
     * <p>
     * решение:
     * Charset cp866 = Charset.forName("CP866");
     * ZipFile file = new ZipFile("archive.zip", cp866);
     */


    public static void main(String[] args) throws IOException
    {


        try
        {
//            test.main(null);
            System.out.println("OK ALL");
            UnzipUtil.main("newArch.zip", "THISWHAT");
            System.out.println("OK ALL");
            System.in.read();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }


        String userDir = System.getProperty("user.dir");
        System.out.println(userDir);
        byte[] buffer = new byte[1024];

        try (ZipInputStream zipIn = new ZipInputStream(new FileInputStream(userDir + "\\newArch.zip")))
        {
            ZipEntry entry;
            String name;
            long size;
            while ((entry = zipIn.getNextEntry()) != null)
            {
                name = entry.getName();
                size = entry.getSize();
                if (entry.isDirectory())
                    System.out.printf("<DIR>   File name: %s \t File size: %d \n", name, size);
                else
                    System.out.printf("        File name: %s \t File size: %d \n", name, size);


                //распаковка:
//                if (entry.isDirectory())
//                {
//                    String sep = File.separator;
//                    File file = new File(userDir + sep + name);
//                    System.out.println(file.getName() + " BOOL " + file.mkdir());
//                }
//                else
//                {
//
//                    FileOutputStream fout = new FileOutputStream(new File(userDir + "\\THISWHAT\\" + name));
//                    int length;
//                    while ((length = zipIn.read(buffer)) != -1)
//                    {
//                        fout.write(buffer, 0, length);
//                    }
//                    fout.close();
//                    zipIn.closeEntry();
//                }
            }

        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }


    }

    public static void mainT(String[] args)
    {
        if (args.length < 2) return;

        File fin = new File(args[0]);
        File fout = new File(args[1]);

        try (ZipOutputStream stream = new ZipOutputStream(new FileOutputStream(fout)))
        {
            System.out.println(" input: " + fin.getAbsolutePath());
            System.out.println("output: " + fout.getAbsolutePath());
            System.out.println("List of files and folders for archiving:");
            for (File f : fin.listFiles())
            {
                if (f.isDirectory()) System.out.println("<DIR>  " + f.getName());
                else System.out.println("       " + f.getName());
            }

            byte[] buffer = new byte[8192];
            doZip(stream, fin, buffer);
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }


        System.out.println("\n\n----------- OUPUT ARCHIVE------------------");
        try (ZipFile zfile = new ZipFile(fout);
                ZipFile winr = new ZipFile("out/winrar.zip"))
        {

            showZip(zfile);
            System.out.println("\n");
            showZip(winr);

        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
        System.out.println("Done.");
    }


    /**
     * Создает ZIP-архив.
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
                ZipEntry entry = new ZipEntry(f.getPath() + "/");  // тут проблема, не создаются директории
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
     * Показывает содержимое ZIP-архива
     *
     * @param file
     */
    public static void showZip(ZipFile file)
    {
        System.out.println("Name: " + file.getName());
        System.out.println("Entries: " + file.size());

        Enumeration<? extends ZipEntry> nm = file.entries();
        System.out.println();

        while (nm.hasMoreElements())
        {
            ZipEntry entry = nm.nextElement();
            if (entry.isDirectory()) System.out.println("<DIR>  " + entry.getName());
            else System.out.println("       " + entry.getName());


//            SimpleDateFormat form = new SimpleDateFormat("yyyy.MM.dd - HH:mm");
//            Date date = new Date(entry.getLastAccessTime().toMillis());
//            System.out.println("\t\t\t\t  entry.getLastAccessTime() = " + entry.getLastAccessTime());
//            Date date = new Date(entry.getLastModifiedTime().toMillis());
//            System.out.println("\t\t\t\tentry.getLastModifiedTime() = " + form.format(date));

        }

    }

    public static void unZip()
    {
    }


}
