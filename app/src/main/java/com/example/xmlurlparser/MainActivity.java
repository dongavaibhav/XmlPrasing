package com.example.xmlurlparser;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        //Call Read rss asyntask to fetch rss
        new ReadRss(MainActivity.this, recyclerView).execute();

    }

    public class ReadRss extends AsyncTask<Void, Void, Void> {

        Context context;
        String address = "https://yts.mx/rss";
        ProgressDialog progressDialog;
        ArrayList<RssItem> rssItems;
        RecyclerView recyclerView;
        URL url;

        public ReadRss(Context context, RecyclerView recyclerView) {
            this.recyclerView = recyclerView;
            this.context = context;
//            progressDialog = new ProgressDialog(context);
//            progressDialog.setMessage("Loading...");
        }

        //before fetching of rss statrs show progress to user
        @Override
        protected void onPreExecute() {
//            progressDialog.show();
            super.onPreExecute();
        }


        //This method will execute in background so in this method download rss feeds
        @Override
        protected Void doInBackground(Void... params) {
            //call process xml method to process document we downloaded from getData() method
            ProcessXml(Getdata());

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
//            progressDialog.dismiss();
            mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    refreshContent();
                    RssAdapter adapter = new RssAdapter(context, rssItems);
                    recyclerView.setLayoutManager(new LinearLayoutManager(context));
                    recyclerView.setAdapter(adapter);
                }
            });
        }

        private void refreshContent() {

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }, 3000);
        }

        // In this method we will process Rss feed  document we downloaded to parse useful information from it
        private void ProcessXml(Document data) {
            if (data != null) {
                rssItems = new ArrayList<>();
                Element root = data.getDocumentElement();
                Node channel = root.getChildNodes().item(1);
                NodeList items = channel.getChildNodes();
                for (int i = 0; i < items.getLength(); i++) {
                    Node cureentchild = items.item(i);
                    if (cureentchild.getNodeName().equalsIgnoreCase("item")) {
                        RssItem item = new RssItem();
                        NodeList itemchilds = cureentchild.getChildNodes();
                        for (int j = 0; j < itemchilds.getLength(); j++) {
                            Node cureent = itemchilds.item(j);
                            if (cureent.getNodeName().equalsIgnoreCase("title")) {
                                item.setTitle(cureent.getTextContent());
                            } else if (cureent.getNodeName().equalsIgnoreCase("description")) {
                                String desc = cureent.getTextContent();
                                item.setDescription(desc.substring(desc.indexOf("/>") + 2));

                                //EXTRACT IMAGE FROM DESCRIPTION
                                String imageUrl = desc.substring(desc.indexOf("src=") + 5, desc.indexOf("jpg") + 3);
                                item.setImageUrl(imageUrl);
//                            item.setDescription(cureent.getTextContent());
                            } else if (cureent.getNodeName().equalsIgnoreCase("link")) {
                                item.setLink(cureent.getTextContent());
                            } else if (cureent.getNodeName().equalsIgnoreCase("pubDate")) {
                                item.setPubDate(cureent.getTextContent());
                            } else if (cureent.getNodeName().equalsIgnoreCase("enclosure")) {
                                //this will return us thumbnail url
                                item.setEnclosure(cureent.getTextContent());
                            }
                        }
                        rssItems.add(item);
                    }
                }
            }
        }

        //This method will download rss feed document from specified url
        public Document Getdata() {
            try {
                url = new URL(address);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                InputStream inputStream = connection.getInputStream();
                DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = builderFactory.newDocumentBuilder();
                Document xmlDoc = builder.parse(inputStream);
                return xmlDoc;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}
