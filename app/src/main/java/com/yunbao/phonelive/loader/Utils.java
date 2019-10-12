package com.yunbao.phonelive.loader;

import java.io.InputStream;
import java.io.OutputStream;

public class Utils {

    public static final  String[] URLS={
        "http://img5.duitang.com/uploads/item/201404/11/20140411214939_XswXa.jpeg",
        "http://att.bbs.duowan.com/forum/201210/20/210446opy9p5pghu015p9u.jpg",
        "https://b-ssl.duitang.com/uploads/item/201505/09/20150509221719_kyNrM.jpeg",
        "https://b-ssl.duitang.com/uploads/item/201709/26/20170926131419_8YhLA.jpeg",
        "https://b-ssl.duitang.com/uploads/item/201505/11/20150511122951_MAwVZ.jpeg",
        "https://b-ssl.duitang.com/uploads/item/201704/23/20170423205828_BhNSv.jpeg",
        "https://b-ssl.duitang.com/uploads/item/201706/30/20170630181644_j4mh5.jpeg",
        "https://b-ssl.duitang.com/uploads/item/201407/22/20140722172759_iPCXv.jpeg",
        "https://b-ssl.duitang.com/uploads/item/201511/11/20151111103149_mrRfd.jpeg",
        "https://b-ssl.duitang.com/uploads/item/201510/14/20151014172010_RnJVz.jpeg"
    };

    public static void CopyStream(InputStream is, OutputStream os)
    {
        final int buffer_size=1024;
        try
        {
            byte[] bytes=new byte[buffer_size];
            for(;;)
            {
              int count=is.read(bytes, 0, buffer_size);
              if(count==-1)
                  break;
              os.write(bytes, 0, count);
            }
        }
        catch(Exception ex){}
    }
}