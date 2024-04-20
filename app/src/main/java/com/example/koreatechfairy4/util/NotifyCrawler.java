package com.example.koreatechfairy4.util;

import com.example.koreatechfairy4.constants.NotifyDomain;
import com.example.koreatechfairy4.dto.NotifyDto;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NotifyCrawler {
    public static List<NotifyDto> getNotice(NotifyDomain domain) throws IOException {
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
            Document detailDoc = Jsoup.connect(detailUrl).get();
            String content = detailDoc.select(".bc-s-post-ctnt-area").text(); // 상세 내용을 선택하는 적절한 셀렉터 입력
            notify.setText(content);

            Elements images = detailDoc.select(".bc-s-post-ctnt-area img");
            if (!images.isEmpty()) {
                ArrayList<String> imageUrls = new ArrayList<>();
                for (Element img : images) {
                    String imageUrl = img.absUrl("src");  // 절대 경로로 변환
                    imageUrls.add(imageUrl);
                }
                notify.setImgUrls(imageUrls);
            }

            list.add(notify);
        }

        return list;
    }
}
