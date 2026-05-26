package com.example.martyrs.config;

import com.example.martyrs.entity.*;
import com.example.martyrs.repository.*;
import com.example.martyrs.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Slf4j
@Component
public class InitRunner implements CommandLineRunner {

    private final AuthService authService;
    private final MartyrBasicInfoRepository martyrRepo;
    private final MartyrDeedRepository deedRepo;
    private final MediaLibraryRepository mediaRepo;
    private final CulturalRelicRepository relicRepo;
    private final HonorMemorialRepository honorRepo;

    public InitRunner(AuthService authService, MartyrBasicInfoRepository martyrRepo,
                      MartyrDeedRepository deedRepo, MediaLibraryRepository mediaRepo,
                      CulturalRelicRepository relicRepo, HonorMemorialRepository honorRepo) {
        this.authService = authService;
        this.martyrRepo = martyrRepo;
        this.deedRepo = deedRepo;
        this.mediaRepo = mediaRepo;
        this.relicRepo = relicRepo;
        this.honorRepo = honorRepo;
    }

    @Override
    public void run(String... args) {
        try {
            authService.initAdmin();
            if (martyrRepo.count() > 0) return;
            seedData();
        } catch (Exception e) {
            log.warn("InitRunner skipped — database not ready: {}", e.getMessage());
        }
    }

    private void seedData() {
        // ========== 烈士 1: 李大钊 ==========
        MartyrBasicInfo m1 = new MartyrBasicInfo();
        m1.setName("李大钊");
        m1.setGender("男");
        m1.setBirthDate(LocalDate.of(1889, 10, 29));
        m1.setDeathDate(LocalDate.of(1927, 4, 28));
        m1.setBirthplace("河北省乐亭县");
        m1.setEthnicity("汉族");
        m1.setPoliticalStatus("中共党员");
        m1.setMilitaryUnit("北京大学");
        m1.setMilitaryRank("教授");
        m1.setSacrificeLocation("北京市西城区");
        m1.setSacrificeReason("被反动军阀杀害");
        m1.setBurialLocation("北京市万安公墓");
        m1.setDescription("中国共产主义运动的先驱，伟大的马克思主义者，杰出的无产阶级革命家，中国共产党的主要创始人之一。");
        m1 = martyrRepo.save(m1);

        deedRepo.save(deed(m1.getId(), "《青春》", "青春之字典，无困难二字；青年之口头，无障碍之语。", "文章", "1916年"));
        deedRepo.save(deed(m1.getId(), "领导五四运动", "1919年，李大钊积极领导和参与了五四运动，在北京大学组织学生运动。", "事迹", "1919年"));
        deedRepo.save(deed(m1.getId(), "创建中国共产党", "1920年，李大钊在北京建立了共产主义小组，为中国共产党成立做出了重要贡献。", "事迹", "1920年"));

        relicRepo.save(relic(m1.getId(), "李大钊手稿", "文献", "纸质", "民国", "后人捐赠", "完好", "中国国家博物馆"));
        relicRepo.save(relic(m1.getId(), "李大钊藏书章", "印章", "玉石", "民国", "征集", "较好", "河北省博物院"));

        honorRepo.save(honor(m1.getId(), "100位为新中国成立作出突出贡献的英雄模范人物", "荣誉称号", "中共中央", "2009年"));
        honorRepo.save(honor(m1.getId(), "中国共产主义运动先驱", "荣誉称号", "中共中央", "1927年"));

        // ========== 烈士 2: 赵一曼 ==========
        MartyrBasicInfo m2 = new MartyrBasicInfo();
        m2.setName("赵一曼");
        m2.setGender("女");
        m2.setBirthDate(LocalDate.of(1905, 10, 25));
        m2.setDeathDate(LocalDate.of(1936, 8, 2));
        m2.setBirthplace("四川省宜宾县");
        m2.setEthnicity("汉族");
        m2.setPoliticalStatus("中共党员");
        m2.setMilitaryUnit("东北抗日联军");
        m2.setMilitaryRank("第三军二团政委");
        m2.setSacrificeLocation("黑龙江省珠河县");
        m2.setSacrificeReason("被日军杀害");
        m2.setBurialLocation("哈尔滨烈士陵园");
        m2.setDescription("抗日民族英雄，曾任东北抗日联军第三军二团政委，在与日寇的斗争中不幸被捕，英勇就义。");
        m2 = martyrRepo.save(m2);

        deedRepo.save(deed(m2.getId(), "抗日游击战", "赵一曼领导抗日游击队在东北地区开展游击战争，给日寇以沉重打击。", "事迹", "1935年"));
        deedRepo.save(deed(m2.getId(), "狱中斗争", "被捕后面对敌人的严刑拷打，赵一曼坚贞不屈，体现了共产党人的崇高气节。", "事迹", "1936年"));

        relicRepo.save(relic(m2.getId(), "赵一曼遗书", "文献", "纸质", "民国", "纪念馆收藏", "一般", "东北烈士纪念馆"));

        honorRepo.save(honor(m2.getId(), "100位为新中国成立作出突出贡献的英雄模范人物", "荣誉称号", "中共中央", "2009年"));

        // ========== 烈士 3: 黄继光 ==========
        MartyrBasicInfo m3 = new MartyrBasicInfo();
        m3.setName("黄继光");
        m3.setGender("男");
        m3.setBirthDate(LocalDate.of(1931, 1, 8));
        m3.setDeathDate(LocalDate.of(1952, 10, 20));
        m3.setBirthplace("四川省中江县");
        m3.setEthnicity("汉族");
        m3.setPoliticalStatus("中共党员");
        m3.setMilitaryUnit("中国人民志愿军");
        m3.setMilitaryRank("第15军135团2营通讯员");
        m3.setSacrificeLocation("朝鲜上甘岭");
        m3.setSacrificeReason("抗美援朝战争牺牲");
        m3.setBurialLocation("辽宁省沈阳市抗美援朝烈士陵园");
        m3.setDescription("中国人民志愿军特级英雄，在上甘岭战役中用自己的胸膛堵住了敌人的枪眼，为部队开辟了前进道路。");
        m3 = martyrRepo.save(m3);

        deedRepo.save(deed(m3.getId(), "上甘岭战役", "1952年10月20日，在上甘岭战役中，黄继光挺身而出，用胸膛堵住了敌人的机枪射孔。", "事迹", "1952年"));

        relicRepo.save(relic(m3.getId(), "黄继光勋章", "勋章", "金属", "1953年", "中央军委颁发", "完好", "中国人民革命军事博物馆"));

        honorRepo.save(honor(m3.getId(), "特级英雄", "荣誉称号", "中国人民志愿军总部", "1953年"));
        honorRepo.save(honor(m3.getId(), "最美奋斗者", "荣誉称号", "中共中央", "2019年"));

        // ========== 烈士 4: 刘胡兰 ==========
        MartyrBasicInfo m4 = new MartyrBasicInfo();
        m4.setName("刘胡兰");
        m4.setGender("女");
        m4.setBirthDate(LocalDate.of(1932, 10, 8));
        m4.setDeathDate(LocalDate.of(1947, 1, 12));
        m4.setBirthplace("山西省文水县");
        m4.setEthnicity("汉族");
        m4.setPoliticalStatus("中共候补党员");
        m4.setMilitaryUnit("文水县抗日民主政府");
        m4.setMilitaryRank("区妇联干事");
        m4.setSacrificeLocation("山西省文水县云周西村");
        m4.setSacrificeReason("被国民党反动派杀害");
        m4.setBurialLocation("山西省文水县刘胡兰纪念馆");
        m4.setDescription("著名革命先烈，15岁英勇就义。毛泽东主席为其题词'生的伟大，死的光荣'。");
        m4 = martyrRepo.save(m4);

        deedRepo.save(deed(m4.getId(), "英勇就义", "1947年1月12日，刘胡兰面对敌人的铡刀，大义凛然，从容就义。", "事迹", "1947年"));

        relicRepo.save(relic(m4.getId(), "刘胡兰塑像", "雕塑", "石膏", "1957年", "纪念馆制作", "完好", "刘胡兰纪念馆"));

        honorRepo.save(honor(m4.getId(), "生的伟大死的光荣", "题词", "毛泽东", "1947年"));

        // ========== 烈士 5: 杨靖宇 ==========
        MartyrBasicInfo m5 = new MartyrBasicInfo();
        m5.setName("杨靖宇");
        m5.setGender("男");
        m5.setBirthDate(LocalDate.of(1905, 2, 13));
        m5.setDeathDate(LocalDate.of(1940, 2, 23));
        m5.setBirthplace("河南省确山县");
        m5.setEthnicity("汉族");
        m5.setPoliticalStatus("中共党员");
        m5.setMilitaryUnit("东北抗日联军");
        m5.setMilitaryRank("第一路军总司令");
        m5.setSacrificeLocation("吉林省濛江县");
        m5.setSacrificeReason("被日军包围后壮烈牺牲");
        m5.setBurialLocation("吉林省通化市杨靖宇烈士陵园");
        m5.setDescription("东北抗日联军的主要创建者和领导人之一，在极其艰苦的条件下坚持抗日游击战争，牺牲后日军剖开他的胃发现只有草根和棉絮。");
        m5 = martyrRepo.save(m5);

        deedRepo.save(deed(m5.getId(), "冰天雪地抗战", "杨靖宇率部在零下四十度的严寒中坚持战斗，以草根树皮充饥。", "事迹", "1940年"));
        deedRepo.save(deed(m5.getId(), "日军震惊", "杨靖宇牺牲后，日军剖开其胃发现只有草根、树皮和棉絮，在场日军无不震惊。", "史料", "1940年"));

        relicRepo.save(relic(m5.getId(), "杨靖宇使用过的手枪", "武器", "金属", "民国", "缴获日军", "一般", "中国人民革命军事博物馆"));
        relicRepo.save(relic(m5.getId(), "杨靖宇印章", "印章", "木制", "民国", "后人捐赠", "破损", "东北烈士纪念馆"));

        honorRepo.save(honor(m5.getId(), "100位为新中国成立作出突出贡献的英雄模范人物", "荣誉称号", "中共中央", "2009年"));
        honorRepo.save(honor(m5.getId(), "中国抗日民族英雄", "荣誉称号", "中共中央", "1940年"));

        // ========== 事迹史料（独立，不特定关联烈士的史料） ==========
        MartyrDeed d = new MartyrDeed();
        d.setTitle("东北抗联历史资料");
        d.setContent("东北抗日联军是在中国共产党领导下的一支英雄部队，在极其艰苦的条件下坚持抗战十四年。");
        d.setDeedType("史料");
        d.setSource("党史研究室");
        d.setDate(LocalDate.of(2020, 1, 1));
        deedRepo.save(d);
    }

    private MartyrDeed deed(Long martyrId, String title, String content, String type, String dateStr) {
        MartyrDeed d = new MartyrDeed();
        d.setMartyrId(martyrId);
        d.setTitle(title);
        d.setContent(content);
        d.setDeedType(type);
        d.setDate(parseDate(dateStr));
        return d;
    }

    private CulturalRelic relic(Long martyrId, String name, String category, String material,
                                String era, String origin, String state, String location) {
        CulturalRelic r = new CulturalRelic();
        r.setMartyrId(martyrId);
        r.setName(name);
        r.setCategory(category);
        r.setMaterial(material);
        r.setEra(era);
        r.setOrigin(origin);
        r.setPreservationState(state);
        r.setLocation(location);
        return r;
    }

    private HonorMemorial honor(Long martyrId, String name, String type, String authority, String dateStr) {
        HonorMemorial h = new HonorMemorial();
        h.setMartyrId(martyrId);
        h.setHonorName(name);
        h.setHonorType(type);
        h.setIssuingAuthority(authority);
        h.setIssueDate(parseDate(dateStr));
        return h;
    }

    private LocalDate parseDate(String str) {
        try {
            return LocalDate.parse(str + "-01-01");
        } catch (Exception e) {
            return LocalDate.now();
        }
    }
}
