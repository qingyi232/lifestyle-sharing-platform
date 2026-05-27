package com.lifestyle.platform.init;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lifestyle.platform.entity.*;
import com.lifestyle.platform.mapper.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private TagMapper tagMapper;
    @Autowired
    private NoteMapper noteMapper;
    @Autowired
    private NoteTagMapper noteTagMapper;
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private LikeMapper likeMapper;
    @Autowired
    private FavoriteMapper favoriteMapper;
    @Autowired
    private FollowMapper followMapper;
    @Autowired
    private NotificationMapper notificationMapper;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public void run(String... args) {
        if (userMapper.selectCount(null) > 0) {
            log.info("数据库已有数据，跳过初始化");
            return;
        }

        log.info("======== 开始初始化示例数据 ========");

        initUsers();
        initCategories();
        initTags();
        initNotes();
        initComments();
        initInteractions();
        initNotifications();

        log.info("======== 示例数据初始化完成 ========");
    }

    private void initUsers() {
        log.info("初始化用户数据...");
        String encodedPassword = passwordEncoder.encode("123456");
        LocalDateTime now = LocalDateTime.now();

        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword(encodedPassword);
        admin.setNickname("管理员");
        admin.setRole(1);
        admin.setStatus(1);
        admin.setAvatar("https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=150&h=150&fit=crop");
        admin.setFollowersCount(0);
        admin.setFollowingCount(0);
        admin.setNotesCount(0);
        admin.setCreatedAt(now.minusDays(30));
        admin.setUpdatedAt(now.minusDays(30));
        userMapper.insert(admin);

        User user2 = new User();
        user2.setUsername("foodie_wang");
        user2.setPassword(encodedPassword);
        user2.setNickname("美食达人小王");
        user2.setRole(0);
        user2.setStatus(1);
        user2.setAvatar("https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=150&h=150&fit=crop");
        user2.setBio("热爱美食，分享生活中的每一道佳肴");
        user2.setEmail("foodie@example.com");
        user2.setFollowersCount(0);
        user2.setFollowingCount(0);
        user2.setNotesCount(0);
        user2.setCreatedAt(now.minusDays(25));
        user2.setUpdatedAt(now.minusDays(25));
        userMapper.insert(user2);

        User user3 = new User();
        user3.setUsername("traveler_li");
        user3.setPassword(encodedPassword);
        user3.setNickname("旅行者小李");
        user3.setRole(0);
        user3.setStatus(1);
        user3.setAvatar("https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150&h=150&fit=crop");
        user3.setBio("用脚步丈量世界，用镜头记录美好");
        user3.setEmail("traveler@example.com");
        user3.setFollowersCount(0);
        user3.setFollowingCount(0);
        user3.setNotesCount(0);
        user3.setCreatedAt(now.minusDays(22));
        user3.setUpdatedAt(now.minusDays(22));
        userMapper.insert(user3);

        User user4 = new User();
        user4.setUsername("fashion_chen");
        user4.setPassword(encodedPassword);
        user4.setNickname("穿搭博主小陈");
        user4.setRole(0);
        user4.setStatus(1);
        user4.setAvatar("https://images.unsplash.com/photo-1438761681033-6461ffad8d80?w=150&h=150&fit=crop");
        user4.setBio("每天不重样的穿搭灵感");
        user4.setEmail("fashion@example.com");
        user4.setFollowersCount(0);
        user4.setFollowingCount(0);
        user4.setNotesCount(0);
        user4.setCreatedAt(now.minusDays(20));
        user4.setUpdatedAt(now.minusDays(20));
        userMapper.insert(user4);

        User user5 = new User();
        user5.setUsername("fitness_zhang");
        user5.setPassword(encodedPassword);
        user5.setNickname("健身达人小张");
        user5.setRole(0);
        user5.setStatus(1);
        user5.setAvatar("https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=150&h=150&fit=crop");
        user5.setBio("坚持健身，享受健康生活");
        user5.setEmail("fitness@example.com");
        user5.setFollowersCount(0);
        user5.setFollowingCount(0);
        user5.setNotesCount(0);
        user5.setCreatedAt(now.minusDays(18));
        user5.setUpdatedAt(now.minusDays(18));
        userMapper.insert(user5);

        User user6 = new User();
        user6.setUsername("home_lin");
        user6.setPassword(encodedPassword);
        user6.setNickname("家居生活小林");
        user6.setRole(0);
        user6.setStatus(1);
        user6.setAvatar("https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=150&h=150&fit=crop");
        user6.setBio("打造温馨小窝，分享家居好物");
        user6.setEmail("home@example.com");
        user6.setFollowersCount(0);
        user6.setFollowingCount(0);
        user6.setNotesCount(0);
        user6.setCreatedAt(now.minusDays(15));
        user6.setUpdatedAt(now.minusDays(15));
        userMapper.insert(user6);
    }

    private void initCategories() {
        log.info("初始化分类数据...");
        String[][] categories = {
                {"美食", "\uD83C\uDF5C", "1"},
                {"旅行", "✈\uFE0F", "2"},
                {"穿搭", "\uD83D\uDC57", "3"},
                {"健身", "\uD83D\uDCAA", "4"},
                {"家居", "\uD83C\uDFE0", "5"},
                {"摄影", "\uD83D\uDCF7", "6"},
                {"读书", "\uD83D\uDCDA", "7"},
                {"美妆", "\uD83D\uDC84", "8"}
        };

        for (String[] cat : categories) {
            Category category = new Category();
            category.setName(cat[0]);
            category.setIcon(cat[1]);
            category.setSortOrder(Integer.parseInt(cat[2]));
            category.setCreatedAt(LocalDateTime.now());
            categoryMapper.insert(category);
        }
    }

    private void initTags() {
        log.info("初始化标签数据...");
        String[] tagNames = {
                "日式料理", "咖啡", "烘焙", "探店",
                "大理", "北海道", "自驾游", "古镇",
                "秋冬穿搭", "通勤", "极简风", "复古",
                "居家健身", "瑜伽", "跑步", "HIIT",
                "改造", "极简主义", "绿植", "收纳",
                "手机摄影", "风光", "治愈系", "好书推荐"
        };

        for (String name : tagNames) {
            Tag tag = new Tag();
            tag.setName(name);
            tagMapper.insert(tag);
        }
    }

    private void initNotes() {
        log.info("初始化笔记数据...");
        LocalDateTime now = LocalDateTime.now();

        // 笔记1: 小王 - 日式料理
        Note note1 = new Note();
        note1.setUserId(2L);
        note1.setTitle("周末在家做了一桌日式料理");
        note1.setCoverImage("https://images.unsplash.com/photo-1579584425555-c3ce17fd4351?w=800");
        note1.setContent("周末的时候心血来潮，决定在家做一桌地道的日式料理。早上去了趟菜市场，买了新鲜的三文鱼、金枪鱼、北极甜虾等刺身食材，还有味噌、海苔、日式酱油等调料。\n\n" +
                "首先做的是味噌汤，用昆布和柴鱼片熬了高汤底，加入嫩豆腐和裙带菜，最后化入味噌，鲜美无比。然后是刺身拼盘，三文鱼切成均匀的薄片，铺在紫苏叶上，配上新鲜的山葵和腌姜，颜值和口感都在线。\n\n" +
                "重头戏是天妇罗，虾仁裹上薄薄的天妇罗粉糊，油温控制在170度左右，炸到金黄酥脆。蘸着天妇罗酱汁，一口下去外酥里嫩。最后还做了日式咖喱饭和玉子烧，一整桌摆出来满满的仪式感。\n\n" +
                "其实日式料理的精髓在于对食材本身味道的尊重，不需要太复杂的调味，新鲜的食材加上简单的处理就能呈现出最好的味道。下次打算挑战寿司卷和拉面，期待！");
        note1.setCategoryId(1L);
        note1.setStatus(1);
        note1.setViewCount(356);
        note1.setLikeCount(0);
        note1.setCommentCount(0);
        note1.setFavoriteCount(0);
        note1.setCreatedAt(now.minusDays(14));
        note1.setUpdatedAt(now.minusDays(14));
        noteMapper.insert(note1);
        addNoteTag(note1.getId(), "日式料理");
        addNoteTag(note1.getId(), "咖啡");

        // 笔记2: 小王 - 咖啡馆
        Note note2 = new Note();
        note2.setUserId(2L);
        note2.setTitle("探店：藏在巷子里的宝藏咖啡馆");
        note2.setCoverImage("https://images.unsplash.com/photo-1495474472287-4d71bcdd2085?w=800");
        note2.setContent("周末偶然发现了一家藏在老城区巷子里的小咖啡馆，门面不大但一推门进去就被温暖的木质装潢和咖啡的香气所吸引。店主是一对热爱咖啡的夫妻，墙上挂满了世界各地咖啡产区的照片。\n\n" +
                "点了一杯手冲肯尼亚AA，用V60冲泡，老板娘手法非常专业。这款豆子带有明显的莓果酸和黑加仑的风味，回甘悠长。又追加了一杯SOE意式浓缩，用的是埃塞俄比亚耶加雪菲水洗豆，做成dirty后奶香和果香交织，层次感非常丰富。\n\n" +
                "甜品也很出彩，巴斯克芝士蛋糕表面焦香，内里是流心状态，搭配咖啡简直绝配。提拉米苏用的是现萃浓缩，酒香和咖啡香恰到好处。\n\n" +
                "店里有二楼的阅读区，阳光透过天窗洒在原木桌面上，翻一本书喝一杯咖啡，就这样消磨一个下午。这种隐藏在城市角落的小店，大概就是生活中的小确幸吧。强烈推荐给喜欢精品咖啡的朋友！");
        note2.setCategoryId(1L);
        note2.setStatus(1);
        note2.setViewCount(289);
        note2.setLikeCount(0);
        note2.setCommentCount(0);
        note2.setFavoriteCount(0);
        note2.setCreatedAt(now.minusDays(12));
        note2.setUpdatedAt(now.minusDays(12));
        noteMapper.insert(note2);
        addNoteTag(note2.getId(), "咖啡");
        addNoteTag(note2.getId(), "探店");

        // 笔记3: 小李 - 大理攻略
        Note note3 = new Note();
        note3.setUserId(3L);
        note3.setTitle("大理古城三日游超详细攻略");
        note3.setCoverImage("https://images.unsplash.com/photo-1488646953014-85cb44e25828?w=800");
        note3.setContent("终于去了心心念念的大理！三天两晚的行程安排得满满当当，把古城和周边的精华景点都打卡了一遍，分享这份超详细攻略。\n\n" +
                "Day1: 到达大理后直奔古城，入住了人民路上的一家白族风格客栈。下午在古城里闲逛，走过五华楼、文献楼，在洋人街喝了一杯鲜花饼奶茶。傍晚骑电动车去了龙龛码头看洱海日落，湖面倒映着苍山的轮廓，美得像画一样。\n\n" +
                "Day2: 早起环洱海一日游。从古城出发，经过才村、喜洲古镇（一定要吃喜洲粑粑！），在海舌生态公园拍了绝美的湖景照。继续沿环海路到双廊，在南诏风情岛看到了经典的白桌椅拍照点。午饭吃了酸辣鱼，鲜嫩酸爽。下午去了小普陀，回程路过挖色镇，又是一波绝美日落。\n\n" +
                "Day3: 上午去了崇圣寺三塔，这是大理的标志性建筑，三塔倒影池是最佳拍照点。下午去了苍山，坐感通索道到半山腰的清碧溪，溪水清澈见底呈碧绿色，洗手池旁的玉带路徒步非常舒服。\n\n" +
                "美食推荐：再回首凉鸡米线、杨记烤乳扇、段公子牛肉火锅。交通建议自驾或租电动车环海最方便。");
        note3.setCategoryId(2L);
        note3.setStatus(1);
        note3.setViewCount(523);
        note3.setLikeCount(0);
        note3.setCommentCount(0);
        note3.setFavoriteCount(0);
        note3.setCreatedAt(now.minusDays(11));
        note3.setUpdatedAt(now.minusDays(11));
        noteMapper.insert(note3);
        addNoteTag(note3.getId(), "大理");
        addNoteTag(note3.getId(), "自驾游");

        // 笔记4: 小李 - 北海道
        Note note4 = new Note();
        note4.setUserId(3L);
        note4.setTitle("冬日里的北海道｜雪国浪漫之旅");
        note4.setCoverImage("https://images.unsplash.com/photo-1491002052546-bf38f186af56?w=800");
        note4.setContent("冬天的北海道是一个纯白的童话世界。这次七天的行程覆盖了札幌、小樽、富良野和旭川，每一站都让人流连忘返。\n\n" +
                "札幌的白色灯饰节刚好赶上，大通公园被数十万盏灯点亮，在白雪的映衬下如梦如幻。狸小路商店街的药妆店是购物天堂，晚上在薄野拉面横丁吃了味噌拉面，浓郁的汤底在寒冷的冬夜暖到心里。\n\n" +
                "小樽运河是此行最浪漫的地方，两岸的石造仓库群在雪中显得格外有韵味。走进一家音乐盒博物馆，精美的八音盒奏出悠扬的旋律。出名的LeTAO双层芝士蛋糕果然名不虚传，入口即化。北一硝子的玻璃工艺品也值得一逛。\n\n" +
                "富良野虽然冬天没有薰衣草花田，但精灵露台（Ningle Terrace）是个隐藏宝藏，小木屋散落在森林中，卖着各种手工艺品，仿佛走进了精灵的世界。旭川的旭山动物园是最后一站，企鹅散步的场景太治愈了！\n\n" +
                "北海道的冬天虽然冷，但正是这份寒冷才让温泉和美食显得格外珍贵。泡着露天温泉看飘落的雪花，大概是冬天最幸福的事了。");
        note4.setCategoryId(2L);
        note4.setStatus(1);
        note4.setViewCount(412);
        note4.setLikeCount(0);
        note4.setCommentCount(0);
        note4.setFavoriteCount(0);
        note4.setCreatedAt(now.minusDays(10));
        note4.setUpdatedAt(now.minusDays(10));
        noteMapper.insert(note4);
        addNoteTag(note4.getId(), "北海道");
        addNoteTag(note4.getId(), "自驾游");

        // 笔记5: 小陈 - 秋冬穿搭
        Note note5 = new Note();
        note5.setUserId(4L);
        note5.setTitle("秋冬穿搭灵感｜温柔知性风");
        note5.setCoverImage("https://images.unsplash.com/photo-1445205170230-053b83016050?w=800");
        note5.setContent("秋冬是最适合玩穿搭的季节，层次感和色彩搭配都有更多发挥空间。今天分享几套我最近常穿的温柔知性风穿搭，适合上班族和学生党。\n\n" +
                "Look 1: 驼色大衣+白色高领毛衣+焦糖色阔腿裤。大衣选择过膝中长款，修饰身形的同时保暖性也很好。内搭白色高领毛衣增加层次感，焦糖色阔腿裤呼应大衣色系，整体是暖色调的同色系搭配，气质又温柔。鞋子配一双米白色切尔西靴。\n\n" +
                "Look 2: 黑色西装外套+条纹衬衫+灰色直筒裤。偏中性的搭配但加了一条珍珠项链增加女性化元素。西装选择了微微oversize的版型，搭配细条纹衬衫知性干练，灰色直筒裤让整体不会太沉闷。\n\n" +
                "Look 3: 奶油白针织开衫+浅蓝色牛仔裤+帆布包。周末休闲风首选，针织开衫选择了宽松的落肩款，随意搭一件白T打底就很好看。浅色牛仔裤增添活力感，配一个帆布包就是慵懒文艺的感觉。\n\n" +
                "穿搭小tips：秋冬色系建议以大地色和莫兰迪色为主，不容易出错。材质方面多选择针织、羊绒、毛呢等有质感的面料，能提升整体高级感。配饰是点睛之笔，简约的耳饰和项链就能让基础款焕发光彩。");
        note5.setCategoryId(3L);
        note5.setStatus(1);
        note5.setViewCount(445);
        note5.setLikeCount(0);
        note5.setCommentCount(0);
        note5.setFavoriteCount(0);
        note5.setCreatedAt(now.minusDays(9));
        note5.setUpdatedAt(now.minusDays(9));
        noteMapper.insert(note5);
        addNoteTag(note5.getId(), "秋冬穿搭");
        addNoteTag(note5.getId(), "极简风");

        // 笔记6: 小陈 - 通勤穿搭
        Note note6 = new Note();
        note6.setUserId(4L);
        note6.setTitle("一周通勤穿搭不重样");
        note6.setCoverImage("https://images.unsplash.com/photo-1487222477894-8943e31ef7b2?w=800");
        note6.setContent("作为一个每天都要在穿什么上纠结的人，我终于摸索出了一套一周通勤穿搭公式，既省时又不会撞衫，分享给和我一样的姐妹们。\n\n" +
                "周一：黑色西装+白色T恤+九分西裤。经典不过时的搭配，用一条金属腰带增加亮点。鞋子选择乐福鞋，干练又舒适。\n\n" +
                "周二：针织马甲+衬衫+半裙。学院风十足的搭配，今年很流行的叠穿法。衬衫选白色或蓝色条纹都好看，半裙选择A字裙型更修饰身材。\n\n" +
                "周三：连衣裙+风衣。最省心的搭配，一条质感好的连衣裙套上风衣就能出门。秋天选卡其色风衣最百搭，春天可以换成浅蓝色或粉色。\n\n" +
                "周四：毛衣+高腰牛仔裤。舒适日穿搭，毛衣选择稍微有设计感的款式（比如泡泡袖、拼色），搭配高腰牛仔裤显腿长。\n\n" +
                "周五：oversize卫衣+小脚裤+帆布鞋。周五放松心情，穿得休闲一点。卫衣选择带印花的款式更活泼，小脚裤平衡上松下紧的比例。\n\n" +
                "关键是建立自己的基础款衣橱，白T、衬衫、针织衫、西裤这些百搭单品多备几件不同颜色，就能自由组合出无数搭配。");
        note6.setCategoryId(3L);
        note6.setStatus(1);
        note6.setViewCount(378);
        note6.setLikeCount(0);
        note6.setCommentCount(0);
        note6.setFavoriteCount(0);
        note6.setCreatedAt(now.minusDays(8));
        note6.setUpdatedAt(now.minusDays(8));
        noteMapper.insert(note6);
        addNoteTag(note6.getId(), "通勤");
        addNoteTag(note6.getId(), "极简风");

        // 笔记7: 小张 - 居家健身
        Note note7 = new Note();
        note7.setUserId(5L);
        note7.setTitle("居家健身30天打卡记录");
        note7.setCoverImage("https://images.unsplash.com/photo-1517836357463-d25dfeac3438?w=800");
        note7.setContent("作为一个长期伏案工作的程序员，我决定挑战30天居家健身计划，不去健身房也能保持好身材。现在30天已经结束，来分享我的训练记录和身体变化。\n\n" +
                "训练安排：每周6练1休，按照推拉腿的分化方式安排。\n周一：胸+三头（俯卧撑变式、臂屈伸）\n周二：背+二头（弹力带划船、引体向上）\n周三：腿（深蹲、弓步蹲、保加利亚蹲）\n周四：肩+核心（侧平举、平板支撑）\n周五：全身HIIT（波比跳、开合跳组合）\n周六：拉伸+瑜伽\n周日：休息\n\n" +
                "饮食方面大致保持了每天1800-2000卡的摄入，增加蛋白质比例，每天至少摄入体重(kg)×1.5g的蛋白质。早餐全麦面包+鸡蛋+牛奶，午餐鸡胸肉+糙米饭+蔬菜，晚餐以蔬菜和瘦肉为主减少碳水。\n\n" +
                "30天下来的变化：体重从72kg降到了69kg，体脂率从22%降到了19%。最明显的变化是腹部线条开始显现，手臂肌肉的轮廓也清晰了不少。更重要的是精力变得更充沛，工作效率也提高了。\n\n" +
                "给想要开始的朋友一些建议：循序渐进不要急于求成，保证睡眠质量，补充足够的水分，坚持比强度更重要！");
        note7.setCategoryId(4L);
        note7.setStatus(1);
        note7.setViewCount(567);
        note7.setLikeCount(0);
        note7.setCommentCount(0);
        note7.setFavoriteCount(0);
        note7.setCreatedAt(now.minusDays(7));
        note7.setUpdatedAt(now.minusDays(7));
        noteMapper.insert(note7);
        addNoteTag(note7.getId(), "居家健身");
        addNoteTag(note7.getId(), "HIIT");

        // 笔记8: 小张 - 瑜伽
        Note note8 = new Note();
        note8.setUserId(5L);
        note8.setTitle("瑜伽初学者入门指南");
        note8.setCoverImage("https://images.unsplash.com/photo-1544367567-0f2fcb009e0b?w=800");
        note8.setContent("最近很多朋友问我怎么开始练瑜伽，作为一个从瑜伽小白到每天坚持练习两年的人，分享一些入门经验，希望能帮到想要开始的你。\n\n" +
                "为什么推荐瑜伽？它不只是拉伸，而是一个身心合一的练习。通过呼吸和体式的配合，能够缓解焦虑、改善体态、增强核心力量。对于久坐办公的人来说，瑜伽是最好的身体修复方式。\n\n" +
                "入门装备：一张防滑瑜伽垫（建议6mm厚度以上）、一套舒适的运动服就够了。不需要买太贵的装备，先开始才是最重要的。\n\n" +
                "新手推荐体式：\n1. 山式（Tadasana）- 所有站立体式的基础，学会正确站立\n2. 下犬式（Adho Mukha Svanasana）- 全身拉伸，增强臂力\n3. 战士一式（Virabhadrasana I）- 增强腿部力量和平衡\n4. 树式（Vrksasana）- 培养专注力和平衡感\n5. 婴儿式（Balasana）- 放松和休息体式\n\n" +
                "练习建议：\n- 每天15-20分钟就够了，贵在坚持\n- 不要和别人比较，关注自己的身体感受\n- 呼吸比体式更重要，始终保持深呼吸\n- 空腹练习效果最好，饭后2小时再练\n- 身体不舒服时不要勉强，学会倾听身体\n\n" +
                "跟练资源推荐：B站有很多优质的瑜伽教程，从基础到进阶都有，找一个你喜欢的老师坚持跟练就好。");
        note8.setCategoryId(4L);
        note8.setStatus(1);
        note8.setViewCount(321);
        note8.setLikeCount(0);
        note8.setCommentCount(0);
        note8.setFavoriteCount(0);
        note8.setCreatedAt(now.minusDays(6));
        note8.setUpdatedAt(now.minusDays(6));
        noteMapper.insert(note8);
        addNoteTag(note8.getId(), "瑜伽");
        addNoteTag(note8.getId(), "居家健身");

        // 笔记9: 小林 - 小户型改造
        Note note9 = new Note();
        note9.setUserId(6L);
        note9.setTitle("小户型改造前后对比｜25㎡也能住出幸福感");
        note9.setCoverImage("https://images.unsplash.com/photo-1502672260266-1c1ef2d93688?w=800");
        note9.setContent("当初租下这个25平米的小开间时，朋友们都说太小了不值得折腾。但我相信空间虽小，用心改造一样能住出品质感。经过两周的改造，来分享一下前后对比和改造思路。\n\n" +
                "改造前的问题：采光不好、储物空间不足、没有独立的工作区域、厨房区域杂乱。\n\n" +
                "改造方案：\n1. 色彩统一：墙面刷成暖白色，家具选择原木色+白色为主色调，视觉上扩大空间感。\n2. 利用垂直空间：墙上安装了三层隔板，放书和植物。床选择了1.2米宽的高架床，床下放书桌和衣柜。\n3. 多功能家具：折叠餐桌平时靠墙，需要时打开。收纳凳既能坐又能储物。\n4. 灯光设计：主灯选择吸顶灯不占空间，加了台灯和氛围灯条，营造温馨感。\n5. 软装点缀：棉麻窗帘、地毯、抱枕都选了莫兰迪色系，统一且温柔。\n\n" +
                "改造花费清单（约3500元）：\n- 墙面涂料+工具：200元\n- 隔板+安装：300元\n- 折叠桌椅：450元\n- 灯具：350元\n- 收纳用品：400元\n- 软装（窗帘、地毯、抱枕等）：800元\n- 绿植：200元\n- 其他杂项：800元\n\n" +
                "改造后的小窝终于有了家的感觉，每天下班回来待在这个温馨的空间里，幸福感满满。小户型不是问题，用心经营才是关键。");
        note9.setCategoryId(5L);
        note9.setStatus(1);
        note9.setViewCount(634);
        note9.setLikeCount(0);
        note9.setCommentCount(0);
        note9.setFavoriteCount(0);
        note9.setCreatedAt(now.minusDays(5));
        note9.setUpdatedAt(now.minusDays(5));
        noteMapper.insert(note9);
        addNoteTag(note9.getId(), "改造");
        addNoteTag(note9.getId(), "收纳");

        // 笔记10: 小林 - 书房
        Note note10 = new Note();
        note10.setUserId(6L);
        note10.setTitle("我的极简主义书房｜提升专注力的秘密");
        note10.setCoverImage("https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=800");
        note10.setContent("作为一个远程办公的自由职业者，书房是我每天待得最久的地方。经过多次调整，终于打造出了一个能让我专注工作的极简书房，分享给同样需要高效工作空间的朋友们。\n\n" +
                "设计理念：less is more。桌面上只保留必要的物品，减少视觉干扰才能提升专注力。\n\n" +
                "桌面布置：\n- 一张1.4米的白橡木桌（定制的，高度刚好75cm）\n- 显示器支架把屏幕抬高到视线水平，减少颈椎压力\n- 一盏可调色温的台灯，白天冷光工作，晚上暖光放松\n- 一个小型绿植（虎尾兰，耐阴好养活）\n- 笔筒和收纳盒各一个，杂物统统收起来\n\n" +
                "收纳系统：\n- 书桌左侧一个五层置物架，分类放书籍和文件\n- 抽屉里用隔板分区，数据线、文具各归其位\n- 每周末做一次桌面清零，把不属于桌面的东西归位\n\n" +
                "氛围营造：\n- 墙面保持纯白，只挂了一幅极简风格的装饰画\n- 窗边放了一把藤编椅，累了可以换个姿势看书\n- 添了一个小型蓝牙音箱，工作时放一些白噪音或轻音乐\n\n" +
                "自从改造了书房之后，工作效率明显提升了。环境真的会影响心情和状态，一个整洁有序的空间能让人更快进入专注模式。");
        note10.setCategoryId(5L);
        note10.setStatus(1);
        note10.setViewCount(298);
        note10.setLikeCount(0);
        note10.setCommentCount(0);
        note10.setFavoriteCount(0);
        note10.setCreatedAt(now.minusDays(4));
        note10.setUpdatedAt(now.minusDays(4));
        noteMapper.insert(note10);
        addNoteTag(note10.getId(), "极简主义");
        addNoteTag(note10.getId(), "绿植");

        // 笔记11: 小王 - 烘焙
        Note note11 = new Note();
        note11.setUserId(2L);
        note11.setTitle("零失败的新手烘焙食谱");
        note11.setCoverImage("https://images.unsplash.com/photo-1486427944781-dbf45f4823fe?w=800");
        note11.setContent("很多朋友想入坑烘焙但怕翻车，今天分享三个我反复试验过的零失败食谱，就算是第一次烤也能成功，做出来颜值和口感都在线！\n\n" +
                "【香蕉马芬蛋糕】\n材料：熟透香蕉2根、鸡蛋2个、低筋面粉180g、糖60g、黄油80g、泡打粉4g\n做法：\n1. 黄油隔水融化，香蕉用叉子压成泥\n2. 鸡蛋加糖搅拌均匀，加入香蕉泥和融化的黄油\n3. 筛入低筋面粉和泡打粉，翻拌均匀（不要过度搅拌）\n4. 倒入马芬模具7分满，175度烤25分钟\n这款蛋糕湿润松软，香蕉的天然甜度让人欲罢不能。\n\n" +
                "【原味司康】\n材料：低筋面粉250g、黄油60g（冷冻切块）、糖30g、盐2g、牛奶100ml、泡打粉6g\n做法：\n1. 面粉、糖、盐、泡打粉混合，加入冷冻黄油块搓成粗砂状\n2. 倒入牛奶揉成团（不要揉太久），擀成2cm厚，用模具切出形状\n3. 表面刷蛋液，190度烤18分钟\n外酥内软，配果酱和奶油是英式下午茶的标配。\n\n" +
                "【巧克力熔岩蛋糕】\n材料：黑巧克力100g、黄油80g、鸡蛋2个、糖40g、低筋面粉30g\n做法：\n1. 巧克力和黄油隔水融化\n2. 鸡蛋加糖打发至浓稠，倒入巧克力液，筛入面粉拌匀\n3. 模具刷油撒粉，倒入面糊，200度烤10-12分钟\n切开后巧克力流心缓缓流出，高级甜品在家就能做！");
        note11.setCategoryId(1L);
        note11.setStatus(1);
        note11.setViewCount(487);
        note11.setLikeCount(0);
        note11.setCommentCount(0);
        note11.setFavoriteCount(0);
        note11.setCreatedAt(now.minusDays(3));
        note11.setUpdatedAt(now.minusDays(3));
        noteMapper.insert(note11);
        addNoteTag(note11.getId(), "烘焙");

        // 笔记12: 小李 - 手机摄影
        Note note12 = new Note();
        note12.setUserId(3L);
        note12.setTitle("手机摄影技巧分享｜日常也能拍出大片感");
        note12.setCoverImage("https://images.unsplash.com/photo-1452587925148-ce544e77e70d?w=800");
        note12.setContent("经常有人问我照片是不是用专业相机拍的，其实大部分都是手机拍摄加后期调色。分享一些我常用的手机摄影技巧，让你的日常照片也能有大片感。\n\n" +
                "构图技巧：\n1. 三分法：打开相机的网格线，把主体放在交叉点上。\n2. 引导线：利用道路、栏杆等线条将视线引导到主体。\n3. 框架构图：利用门框、窗户、树木形成天然画框。\n4. 留白：给画面留出呼吸空间，少即是多。\n\n" +
                "光影运用：\n- 黄金时刻（日出后、日落前1小时）的光线最柔和，拍人像和风景都好看\n- 阴天是最好的柔光灯，拍食物和小物件阴天出片率更高\n- 利用光影对比，逆光拍摄可以创造梦幻氛围\n- 善用窗边的自然光，一面窗就是一个天然影棚\n\n" +
                "后期调色：\n- 推荐使用Snapseed和VSCO\n- 降低对比度+提高亮部暗部+加一点颗粒感=胶片风格\n- 统一色调很重要，找到适合自己的调色风格保持一致性\n- 不要过度P图，保持自然质感\n\n" +
                "拍摄角度：\n- 食物：45度俯拍或完全俯拍最显高级\n- 建筑：低角度仰拍更有气势\n- 人像：侧面45度最显脸小\n- 风景：加入前景元素增加层次感\n\n" +
                "最重要的是多拍多练，手机摄影没有那么多条条框框，记录生活中的美好瞬间本身就是最好的摄影。");
        note12.setCategoryId(6L);
        note12.setStatus(1);
        note12.setViewCount(402);
        note12.setLikeCount(0);
        note12.setCommentCount(0);
        note12.setFavoriteCount(0);
        note12.setCreatedAt(now.minusDays(2));
        note12.setUpdatedAt(now.minusDays(2));
        noteMapper.insert(note12);
        addNoteTag(note12.getId(), "手机摄影");
        addNoteTag(note12.getId(), "治愈系");

        // 待审核笔记1
        Note pending1 = new Note();
        pending1.setUserId(2L);
        pending1.setTitle("这家新开的日料店也太好吃了吧");
        pending1.setCoverImage("https://images.unsplash.com/photo-1553621042-f6e147245754?w=800");
        pending1.setContent("今天发现了一家新开的日料店，环境超级好，食材也很新鲜。推荐他们家的刺身拼盘和烤鳗鱼饭，价格也很合理。老板说食材都是每天从市场新鲜采购的，难怪味道这么正宗。吃完之后还送了一份甜品，服务态度也很棒。下次一定还会再来，也推荐给大家试试！地址就在市中心商圈附近，交通很方便。");
        pending1.setCategoryId(1L);
        pending1.setStatus(0);
        pending1.setViewCount(0);
        pending1.setLikeCount(0);
        pending1.setCommentCount(0);
        pending1.setFavoriteCount(0);
        pending1.setCreatedAt(now.minusDays(1));
        pending1.setUpdatedAt(now.minusDays(1));
        noteMapper.insert(pending1);

        // 待审核笔记2
        Note pending2 = new Note();
        pending2.setUserId(4L);
        pending2.setTitle("春季新款穿搭分享");
        pending2.setCoverImage("https://images.unsplash.com/photo-1515886657613-9f3515b0c78f?w=800");
        pending2.setContent("春天来了，是时候换上轻薄的春装啦！今天分享几套最近入手的春季新款穿搭，碎花裙配针织开衫，温柔又浪漫。还有清新的薄荷绿卫衣配白色百褶裙，满满的少女感。春天的色彩就是要明亮活泼，告别沉闷的冬装，拥抱阳光！");
        pending2.setCategoryId(3L);
        pending2.setStatus(0);
        pending2.setViewCount(0);
        pending2.setLikeCount(0);
        pending2.setCommentCount(0);
        pending2.setFavoriteCount(0);
        pending2.setCreatedAt(now.minusHours(12));
        pending2.setUpdatedAt(now.minusHours(12));
        noteMapper.insert(pending2);

        // 待审核笔记3
        Note pending3 = new Note();
        pending3.setUserId(5L);
        pending3.setTitle("跑步一个月的感受");
        pending3.setCoverImage("https://images.unsplash.com/photo-1571008887538-b36bb32f4571?w=800");
        pending3.setContent("从上个月开始每天早上晨跑5公里，坚持了一个月来说说感受。最大的变化就是精神状态好了很多，以前上班总是犯困，现在精力充沛。体重也从75降到了73公斤，虽然不多但是能明显感觉到裤腰松了。跑步真的是最简单也最有效的运动方式，不需要任何器械一双跑鞋就够了。");
        pending3.setCategoryId(4L);
        pending3.setStatus(0);
        pending3.setViewCount(0);
        pending3.setLikeCount(0);
        pending3.setCommentCount(0);
        pending3.setFavoriteCount(0);
        pending3.setCreatedAt(now.minusHours(6));
        pending3.setUpdatedAt(now.minusHours(6));
        noteMapper.insert(pending3);

        // 已驳回笔记
        Note rejected = new Note();
        rejected.setUserId(6L);
        rejected.setTitle("某品牌智能台灯开箱测评");
        rejected.setCoverImage("https://images.unsplash.com/photo-1507473885765-e6ed057ab6fe?w=800");
        rejected.setContent("今天给大家推荐一款超好用的智能台灯，是某品牌赞助我做的测评。这款台灯支持语音控制、色温调节、定时关灯等功能。做工精致，颜值也很高，放在书桌上既实用又好看。现在品牌方给了我一个专属优惠码，通过我的链接购买可以享受8折优惠哦！赶快下单吧～");
        rejected.setCategoryId(5L);
        rejected.setStatus(2);
        rejected.setRejectReason("内容涉及广告推广，请修改后重新提交");
        rejected.setViewCount(0);
        rejected.setLikeCount(0);
        rejected.setCommentCount(0);
        rejected.setFavoriteCount(0);
        rejected.setCreatedAt(now.minusDays(2));
        rejected.setUpdatedAt(now.minusDays(1));
        noteMapper.insert(rejected);
    }

    private void addNoteTag(Long noteId, String tagName) {
        LambdaQueryWrapper<Tag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Tag::getName, tagName);
        Tag tag = tagMapper.selectOne(wrapper);
        if (tag != null) {
            NoteTag noteTag = new NoteTag();
            noteTag.setNoteId(noteId);
            noteTag.setTagId(tag.getId());
            noteTagMapper.insert(noteTag);
        }
    }

    private void initComments() {
        log.info("初始化评论数据...");
        LocalDateTime now = LocalDateTime.now();

        // 笔记1的评论
        insertComment(1L, 3L, null, "看起来好精致！日式料理果然很讲究摆盘，收藏了食谱改天试试", now.minusDays(13));
        insertComment(1L, 4L, null, "味噌汤是我最爱！下次可以试试加入豆皮，口感更丰富", now.minusDays(13).plusHours(2));
        Long c1r = insertComment(1L, 5L, null, "天妇罗的油温控制确实很关键，我之前炸糊了好几次", now.minusDays(12).plusHours(5));
        insertComment(1L, 2L, c1r, "建议用温度计控制油温，170-180度最佳，入锅时冒小泡泡就差不多了", now.minusDays(12).plusHours(8));

        // 笔记2的评论
        insertComment(2L, 6L, null, "这种隐藏的小店最有feel了，请问具体在哪个区呀？", now.minusDays(11));
        Long c2r = insertComment(2L, 3L, null, "巴斯克芝士蛋糕配手冲绝了！这家店我也去过，超喜欢他们家的空间", now.minusDays(11).plusHours(3));
        insertComment(2L, 2L, c2r, "对呀！二楼那个阅读角是我最喜欢的位置", now.minusDays(11).plusHours(5));
        insertComment(2L, 5L, null, "看了你的分享直接想去打卡，周末安排！", now.minusDays(10));

        // 笔记3的评论
        insertComment(3L, 2L, null, "攻略好详细！正好计划五一去大理，这下有参考了", now.minusDays(10));
        insertComment(3L, 4L, null, "喜洲粑粑真的绝！破酥的那种一定要吃", now.minusDays(10).plusHours(4));
        Long c3r = insertComment(3L, 5L, null, "环洱海一天够吗？感觉好多地方想去", now.minusDays(9));
        insertComment(3L, 3L, c3r, "一天基本够的，但建议早点出发。如果时间充裕可以分两天，东边和西边各一天", now.minusDays(9).plusHours(2));

        // 笔记4的评论
        insertComment(4L, 2L, null, "企鹅散步也太可爱了！好想去北海道看看", now.minusDays(9));
        insertComment(4L, 6L, null, "冬天的小樽运河太浪漫了，好像电影画面", now.minusDays(9).plusHours(3));
        insertComment(4L, 4L, null, "收藏了！请问北海道冬天大概要准备多厚的衣服？", now.minusDays(8));

        // 笔记5的评论
        insertComment(5L, 2L, null, "驼色大衣+白色高领毛衣这套也太好看了！种草", now.minusDays(8));
        insertComment(5L, 6L, null, "穿搭小白表示学到了！大地色系确实不容易出错", now.minusDays(8).plusHours(4));
        Long c5r = insertComment(5L, 3L, null, "请问驼色大衣有推荐的品牌吗？", now.minusDays(7));
        insertComment(5L, 4L, c5r, "优衣库和ZARA的都不错，性价比很高。如果预算高一些可以看看MaxMara", now.minusDays(7).plusHours(3));

        // 笔记6的评论
        insertComment(6L, 5L, null, "一周穿搭公式太实用了！我每天也为穿什么烦恼", now.minusDays(7));
        insertComment(6L, 3L, null, "周三的连衣裙+风衣确实最省心，早上多睡10分钟", now.minusDays(7).plusHours(5));

        // 笔记7的评论
        insertComment(7L, 2L, null, "30天就能看到这么明显的变化？太励志了！", now.minusDays(6));
        insertComment(7L, 3L, null, "请问波比跳一组做多少个？感觉很累", now.minusDays(6).plusHours(2));
        Long c7r = insertComment(7L, 6L, null, "我也想试试，但总是坚持不下来怎么办", now.minusDays(5));
        insertComment(7L, 5L, c7r, "建议先从每天10分钟开始，养成习惯后再慢慢加量。找个打卡的小伙伴互相监督也很有效", now.minusDays(5).plusHours(3));

        // 笔记8的评论
        insertComment(8L, 4L, null, "一直想学瑜伽，这个入门指南太及时了！", now.minusDays(5));
        insertComment(8L, 6L, null, "坚持练瑜伽一年了，确实对缓解焦虑很有帮助", now.minusDays(5).plusHours(4));
        insertComment(8L, 2L, null, "请问有推荐的B站瑜伽老师吗？", now.minusDays(4));

        // 笔记9的评论
        insertComment(9L, 2L, null, "25平米能改成这样真的太厉害了！收纳功力满级", now.minusDays(4));
        insertComment(9L, 3L, null, "花3500就改造成这样太值了！请问那个折叠桌在哪买的", now.minusDays(4).plusHours(3));
        Long c9r = insertComment(9L, 5L, null, "高架床睡着会不会摇晃呀？", now.minusDays(3));
        insertComment(9L, 6L, c9r, "我选的是钢架结构的，很稳。建议选承重能力强一些的款式就不会有问题", now.minusDays(3).plusHours(2));

        // 笔记10的评论
        insertComment(10L, 4L, null, "极简书房太对我的审美了！整洁的桌面确实能提升工作效率", now.minusDays(3));
        insertComment(10L, 5L, null, "一直想布置一个这样的工作角，感谢分享思路", now.minusDays(3).plusHours(5));

        // 笔记11的评论
        insertComment(11L, 3L, null, "香蕉马芬我做了，真的零失败！家人都说好吃", now.minusDays(2));
        insertComment(11L, 6L, null, "巧克力熔岩蛋糕的流心效果好棒！关键是烤的时间吗？", now.minusDays(2).plusHours(3));
        Long c11r = insertComment(11L, 4L, null, "请问低筋面粉可以用中筋代替吗？", now.minusDays(1));
        insertComment(11L, 2L, c11r, "可以的，中筋面粉加20%的玉米淀粉混合就接近低筋面粉的效果了", now.minusDays(1).plusHours(2));

        // 笔记12的评论
        insertComment(12L, 4L, null, "三分法构图我一直在用，简单但很有效！", now.minusDays(1));
        insertComment(12L, 6L, null, "推荐一个调色app叫Lightroom Mobile，免费版就很够用", now.minusDays(1).plusHours(4));
        insertComment(12L, 5L, null, "黄金时刻拍照确实好看，但要早起真的太难了", now.minusHours(18));
    }

    private Long insertComment(Long noteId, Long userId, Long parentId, String content, LocalDateTime time) {
        Comment comment = new Comment();
        comment.setNoteId(noteId);
        comment.setUserId(userId);
        comment.setParentId(parentId);
        comment.setContent(content);
        comment.setCreatedAt(time);
        commentMapper.insert(comment);
        return comment.getId();
    }

    private void initInteractions() {
        log.info("初始化互动数据...");
        LocalDateTime now = LocalDateTime.now();

        // 点赞记录 (25条)
        long[][] likes = {
                {3, 1}, {4, 1}, {5, 1}, {6, 1},
                {2, 3}, {4, 3}, {5, 3}, {6, 3},
                {2, 4}, {3, 4}, {6, 4},
                {2, 5}, {3, 5}, {5, 5}, {6, 5},
                {2, 6}, {3, 6},
                {2, 7}, {3, 7}, {4, 7}, {6, 7},
                {4, 8}, {6, 8},
                {2, 9}, {3, 9}, {4, 9}, {5, 9},
                {4, 10}, {5, 10},
                {3, 11}, {5, 11}, {6, 11}
        };

        for (long[] l : likes) {
            UserLike like = new UserLike();
            like.setUserId(l[0]);
            like.setNoteId(l[1]);
            like.setCreatedAt(now.minusDays((int) (Math.random() * 10)));
            likeMapper.insert(like);
        }

        // 收藏记录 (12条)
        long[][] favorites = {
                {3, 1}, {5, 1},
                {6, 2},
                {2, 3}, {4, 3},
                {2, 5}, {6, 5},
                {3, 7}, {4, 7},
                {2, 9}, {5, 9},
                {4, 12}
        };

        for (long[] f : favorites) {
            Favorite favorite = new Favorite();
            favorite.setUserId(f[0]);
            favorite.setNoteId(f[1]);
            favorite.setCreatedAt(now.minusDays((int) (Math.random() * 10)));
            favoriteMapper.insert(favorite);
        }

        // 关注关系 (10条)
        long[][] follows = {
                {2, 3}, {2, 4},
                {3, 2}, {3, 5},
                {4, 2}, {4, 6},
                {5, 3}, {5, 6},
                {6, 2}, {6, 4}
        };

        for (long[] fo : follows) {
            Follow follow = new Follow();
            follow.setFollowerId(fo[0]);
            follow.setFollowingId(fo[1]);
            follow.setCreatedAt(now.minusDays((int) (Math.random() * 20)));
            followMapper.insert(follow);
        }

        // 更新笔记的点赞数、评论数、收藏数
        updateNoteStats(1L, 4, 4, 2);
        updateNoteStats(2L, 0, 4, 1);
        updateNoteStats(3L, 4, 4, 2);
        updateNoteStats(4L, 3, 3, 0);
        updateNoteStats(5L, 4, 4, 2);
        updateNoteStats(6L, 2, 2, 0);
        updateNoteStats(7L, 4, 4, 2);
        updateNoteStats(8L, 2, 3, 0);
        updateNoteStats(9L, 4, 4, 2);
        updateNoteStats(10L, 2, 2, 0);
        updateNoteStats(11L, 3, 4, 0);
        updateNoteStats(12L, 0, 3, 1);

        // 更新用户的粉丝数、关注数、笔记数
        updateUserStats(2L, 3, 2, 3);  // 小王: 3粉丝 2关注 3笔记
        updateUserStats(3L, 2, 2, 3);  // 小李: 2粉丝 2关注 3笔记
        updateUserStats(4L, 2, 2, 2);  // 小陈: 2粉丝 2关注 2笔记
        updateUserStats(5L, 1, 2, 2);  // 小张: 1粉丝 2关注 2笔记
        updateUserStats(6L, 2, 2, 2);  // 小林: 2粉丝 2关注 2笔记
    }

    private void updateNoteStats(Long noteId, int likeCount, int commentCount, int favoriteCount) {
        Note note = noteMapper.selectById(noteId);
        if (note != null) {
            note.setLikeCount(likeCount);
            note.setCommentCount(commentCount);
            note.setFavoriteCount(favoriteCount);
            noteMapper.updateById(note);
        }
    }

    private void updateUserStats(Long userId, int followersCount, int followingCount, int notesCount) {
        User user = userMapper.selectById(userId);
        if (user != null) {
            user.setFollowersCount(followersCount);
            user.setFollowingCount(followingCount);
            user.setNotesCount(notesCount);
            userMapper.updateById(user);
        }
    }

    private void initNotifications() {
        log.info("初始化通知数据...");
        LocalDateTime now = LocalDateTime.now();

        insertNotification(2L, 3L, "like", 1L, "旅行者小李 赞了你的笔记", 1, now.minusDays(10));
        insertNotification(2L, 4L, "like", 1L, "穿搭博主小陈 赞了你的笔记", 0, now.minusDays(9));
        insertNotification(3L, 2L, "comment", 3L, "美食达人小王 评论了你的笔记", 1, now.minusDays(8));
        insertNotification(4L, 2L, "follow", null, "美食达人小王 关注了你", 1, now.minusDays(7));
        insertNotification(5L, 3L, "like", 7L, "旅行者小李 赞了你的笔记", 0, now.minusDays(6));
        insertNotification(6L, 4L, "follow", null, "穿搭博主小陈 关注了你", 0, now.minusDays(5));
        insertNotification(2L, 6L, "comment", 9L, "家居生活小林 评论了你的笔记", 0, now.minusDays(4));
        insertNotification(3L, 5L, "follow", null, "健身达人小张 关注了你", 0, now.minusDays(3));
    }

    private void insertNotification(Long userId, Long fromUserId, String type, Long noteId, String content, int isRead, LocalDateTime time) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setFromUserId(fromUserId);
        notification.setType(type);
        notification.setNoteId(noteId);
        notification.setContent(content);
        notification.setIsRead(isRead);
        notification.setCreatedAt(time);
        notificationMapper.insert(notification);
    }
}
