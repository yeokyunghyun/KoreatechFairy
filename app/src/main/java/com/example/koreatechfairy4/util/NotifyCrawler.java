package com.example.koreatechfairy4.util;

import com.example.koreatechfairy4.constants.NotifyDomain;
import com.example.koreatechfairy4.dto.NotifyDto;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class NotifyCrawler {

    public static List<NotifyDto> getNotice(NotifyDomain domain) throws IOException, NoSuchAlgorithmException, KeyManagementException {
        List<NotifyDto> list = new ArrayList<>();

        String aunuriURL = "https://portal.koreatech.ac.kr/ctt/bb/bulletin?b=" + domain.link();

        Document doc = Jsoup.connect(aunuriURL).get();

        Elements rows = doc.select("tr[data-name='post_list']");

        for (Element row : rows) {
            String detailUrl = "https://portal.koreatech.ac.kr" + row.attr("data-url");
            int notifyNum = Integer.parseInt(row.select(".bc-s-post_seq").text().trim());
            String title = row.select(".bc-s-title span").attr("title");
            String date = row.select(".bc-s-cre_dt").text().trim();
            String author = row.select(".bc-s-cre_user_name").text().trim();

            NotifyDto notify = new NotifyDto();
            notify.setTitle(title);
            notify.setNotifyNum(notifyNum);
            notify.setDate(date);
            notify.setAuthor(author);

            // 내용은 상세 페이지에서 가져와야 함

            // 내용 가져오기 (옵션)
            setSSL();
            Document detailDoc = Jsoup.connect(detailUrl).get();
            Elements detailElement = detailDoc.select(".bc-s-post-ctnt-area");
            String content = detailElement.text();
            detailElement.select("img").remove();

            Elements images = detailDoc.select(".bc-s-post-ctnt-area img");
            if (!images.isEmpty()) {
                ArrayList<String> imageUrls = new ArrayList<>();
                for (Element img : images) {
                    String imageUrl = img.absUrl("src");  // 절대 경로로 변환
                    imageUrls.add(imageUrl);
                }
                notify.setImgUrls(imageUrls);
            }

            Element htmlElement = detailElement.first();
            htmlElement.select("img").remove();
            String html = htmlElement.html();
            notify.setText(content);
            notify.setHtml(html);

            list.add(notify);
        }

        return list;
    }

    public static List<NotifyDto> getJobNotice(String link) throws IOException, NoSuchAlgorithmException, KeyManagementException {
        List<NotifyDto> list = new ArrayList<>();

        Document doc = Jsoup.connect(link).get();

        Elements rows = doc.select("tr.primeLine");

        for (Element row : rows) {
            String detailUrl = "https://job.koreatech.ac.kr/" + row.select("td.title a").attr("href");
            String title = row.select("td.title a b").text();
            //Elements cells = row.select("td.center.none");
            String date = row.select("#contents > table > tbody > tr:nth-child(1) > td:nth-child(3)").text().trim();
            String author = row.select("#contents > table > tbody > tr:nth-child(1) > td:nth-child(5)").text().trim();
            //String date = cells.get(1).text();
            //String author = cells.get(2).text();

            NotifyDto notify = new NotifyDto();
            notify.setTitle(title);
            notify.setDate(date);
            notify.setAuthor(author);

            // 내용은 상세 페이지에서 가져와야 함

            // 내용 가져오기 (옵션)
            setSSL();
            Document detailDoc = Jsoup.connect(detailUrl).get();
            Elements detailElement = detailDoc.select(".content");
            String text = detailElement.text();

            Elements images = detailDoc.select("td[colspan='2'] img");
            if (!images.isEmpty()) {
                ArrayList<String> imageUrls = new ArrayList<>();
                for (Element img : images) {
                    String imageUrl = img.absUrl("src");  // 절대 경로로 변환
                    imageUrls.add(imageUrl);
                }
                notify.setImgUrls(imageUrls);
            }

            Element htmlElement = detailElement.first();
            htmlElement.select("img").remove();
            String html = htmlElement.html();
            notify.setText(text);
            notify.setHtml(html);

            list.add(notify);
        }

        return list;
    }

    public static void setSSL() throws NoSuchAlgorithmException, KeyManagementException {
        TrustManager[] trustAllCerts = new TrustManager[] {
                new X509TrustManager() {
                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        // TODO Auto-generated method stub
                        return null;
                    }
                    @Override
                    public void checkClientTrusted(X509Certificate[] chain, String authType)
                            throws CertificateException {
                        // TODO Auto-generated method stub

                    }
                    @Override
                    public void checkServerTrusted(X509Certificate[] chain, String authType)
                            throws CertificateException {
                        // TODO Auto-generated method stub
                    }
                }
        };
        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new SecureRandom());
        HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        });
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
    }


}
