package derv.com.rssfeed;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Created by dlark on 20/03/2017.
 */

public class ReadRss extends AsyncTask <Void, Void, Void> {
    Context context;
    String urlStr = "http://www.independent.ie/breaking-news/irish-news/?service=Rss";
    ProgressDialog pDialog;
    URL url;

    public ReadRss(Context context) {
        this.context = context;
        pDialog = new ProgressDialog(context);
        pDialog.setMessage("Loading...");
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pDialog.show();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        pDialog.dismiss();
        super.onPostExecute(aVoid);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        ProcessXml(GetData());
        return null;
    }

    private void ProcessXml(Document data) {
        if(data != null) {
            ArrayList<FeedItem> feedItems = new ArrayList<FeedItem>();
            Element root = data.getDocumentElement();
            Node channel = root.getChildNodes().item(1);
            NodeList items = channel.getChildNodes();
            for(int i = 0; i<items.getLength();i++)
            {
                Node currentChild = items.item(i);
                if(currentChild.getNodeName().equalsIgnoreCase("item")){
                    FeedItem item = new FeedItem();
                    NodeList itemChilds = currentChild.getChildNodes();
                    for(int j=0;j<itemChilds.getLength();j++) {
                        Node current = itemChilds.item(j);
                        if(current.getNodeName().equalsIgnoreCase("title")){
                            item.setTitle(current.getTextContent());
                        } else if(current.getNodeName().equalsIgnoreCase("link")) {
                            item.setLink(current.getTextContent());
                        } else if(current.getNodeName().equalsIgnoreCase("description")) {
                            item.setDescription(current.getTextContent());
                        } else if(current.getNodeName().equalsIgnoreCase("pubDate")) {
                            item.setPubDate(current.getTextContent());
                        } //else if(current.getNodeName().equalsIgnoreCase("thumbnailUrl")) {
//                            String url = current.getAttributes().item(0).getTextContent();
//                            item.setThumbnailUrl(url);
//                        }
                    }
                    feedItems.add(item);
//                    Log.d("itemThumbnailUrl", item.getThumbnailUrl());
                }
            }
        }
    }

    public Document GetData() {
        try {
            url = new URL(urlStr);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(10 * 1000);
            connection.setConnectTimeout(10 * 1000);
            connection.setRequestMethod("GET");
            InputStream in = connection.getInputStream();
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            Document xmlDoc = builder.parse(in);
            return xmlDoc;
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
