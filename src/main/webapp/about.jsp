<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>
<%@ taglib tagdir="/WEB-INF/tags/static" prefix="ts" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="teammates.common.util.Const" %>
<c:set var="contactPage" value="<%= Const.ViewURIs.CONTACT %>" />
<t:staticPage jsIncludes="${jsIncludes}" currentPage="about">
    <h1 id="caption">About Us</h1>
    <div id="contentHolder">
        <p class="bold">Acknowledgements:</p>
        <br>
        <h2>
            <span class="bold">TEAMMATES</span> team wishes to thank the following invaluable contributions.
        </h2>
        <div style="margin: 0 auto; padding: 0 52px 15px;">
            <br>
            <ul>
                <li style="margin: 0 0 10px 0;">
                    <a href="http://www.comp.nus.edu.sg/">School of Computing, National University of Singapore (NUS)</a>, for providing us with the infrastructure support to run the project.
                </li>
                <li style="margin: 0 0 10px 0;">
                    <a href="http://www.cdtl.nus.edu.sg/">Centre for Development of Teaching and Learning (CDTL)</a> of NUS, for supporting us with several Teaching Enhancement Grants over the years.
                </li>
                <li style="margin: 0 0 10px 0;">
                    <span class="bold">Learning Innovation Fund-Technology (LIF-T)</span> initiative of NUS, for funding us for the 2015-2018 period.
                </li>
                <li style="margin: 0 0 10px 0;">
                    <span class="bold">Google Summer of Code</span> Program, for including TEAMMATES as a mentor organization in GSoC2014 and GSoC2015 editions.
                </li>
                <li style="margin: 0 0 10px 0;">
                    <span class="bold">Facebook Open Academy</span> Program, for including TEAMMATES as a mentor organization in FBOA 2016.
                </li>
                <li style="margin: 0 0 10px 0;">
                    <span class="bold">YourKit LLC</span>, for providing us with free licenses for the <a href="https://www.yourkit.com/.net/profiler/index.jsp">YourKit Java Profiler</a>
                    <img src="https://www.yourkit.com/images/yklogo.png" width='70'>.
                </li>
            </ul>
        </div>

        <p class="bold">Core Team:</p>
        <table>
            <c:set var="descDamith">
                <a href="http://www.comp.nus.edu.sg/~damithch">Damith C. Rajapakse</a><br><br>
                Founder (2010)<br>
                Project Mentor (2010 -)
            </c:set>
            <ts:teamLead imgSuffix="damith" desc="${descDamith}" />
            <c:set var="descWeilin">
                Low WeiLin<br><br>
                Project Lead (Aug 2015 - )<br>
                Area Lead - Sessions (Aug 2014 - Jul 2015)<br>
                Snr Developer (June 2014 - Jul 2014)<br>
                Committer (Jan 2014 - Jul 2014)
            </c:set>
            <ts:teamLead imgSuffix="weilin" desc="${descWeilin}" />
            <c:set var="descHongjin">
                Kang Hong Jin<br><br>
                Project Lead (Aug 2015 - )<br>
                Snr Developer (May 2015 - Jul 2015)<br>
                Committer (Aug 2014 - Apr 2015)
            </c:set>
            <ts:teamLead imgSuffix="hongjin" desc="${descHongjin}" />
            <c:set var="descThyagesh">
                Thyagesh Manikandan<br><br>
                Project Lead (Jan 2016 - )<br>
                Area Lead - Profiles (Aug 2014 - Dec 2015)<br>
                Snr Developer (June 2014 - Jul 2014)<br>
                Committer (Apr 2014 - May 2014)
            </c:set>
            <ts:teamLead imgSuffix="thyagesh" desc="${descThyagesh}" />
        </table>
        
        <br><br><br><br><br>

        <table>
            <c:set var="descJosephine">
                Josephine Kwa<br>
                Area Lead - UI/UX, Comments, Profiles, Courses (Jan 2016 - )<br>
                Snr Developer (Aug 2015 - Dec 2015)<br>
                Committer (Feb 2015 - May 2015)
            </c:set>
            <ts:areaLead imgSuffix="josephine" desc="${descJosephine}" />
            <c:set var="descTania">
                Tania Chattopadhyay<br>
                Area Lead - Access control, Search, Email (Jan 2016 - )<br>
                Snr Developer (Aug 2015 - Dec 2015)<br>
                Committer (Feb 2015 - May 2015)
            </c:set>
            <ts:areaLead imgSuffix="tania" desc="${descTania}" />
            <c:set var="descKhanh">
                Truong Ngoc Khanh<br>
                Area Lead - Admin, Scalability (Jan 2016 - )<br>
                Snr Developer (Aug 2015 - Dec 2015)<br>
                Committer (Jan 2015 - May 2015)
            </c:set>
            <ts:areaLead imgSuffix="khanh" desc="${descKhanh}" />
            <c:set var="descYoujun">
                Soh You Jun<br>
                Area Lead - UI/UX, Submissions, Courses (Jan 2016 - )<br>
                Snr Developer (Aug 2015 - Dec 2015)<br>
                Committer (Apr 2015 - May 2015)
            </c:set>
            <ts:areaLead imgSuffix="youjun" desc="${descYoujun}" />
            <c:set var="descWilson">
                Wilson Kurniawan<br>
                Area Lead - DevOps, Results (Jan 2016 - )<br>
                Snr Developer (Aug 2015 - Dec 2015)<br>
                Committer (Apr 2015 - May 2015)
            </c:set>
            <ts:areaLead imgSuffix="wilson" desc="${descWilson}" />
            <c:set var="descJunhao">
                Yap Jun Hao<br>
                Area Lead - DevOps, Submissions (Jan 2016 - )<br>
                Snr Developer (Aug 2015 - Dec 2015)<br>
                Committer (Apr 2015 - May 2015)
            </c:set>
            <ts:areaLead imgSuffix="junhao" desc="${descJunhao}" />
        </table>
      
        <br><br>

        <table>
            <c:set var="descAshray">
                Ashray Jain<br><br>
                Snr Developer (Oct 2015 - )<br>
                Committer (Aug 2015 - Sep 2015)
                <br><br>
            </c:set>
            <ts:snrDev imgSuffix="ashray" desc="${descAshray}" />
            <c:set var="descNimantha">
                Nimantha Baranasuriya<br><br>
                Project Admin (2013 - )
                <br>
            </c:set>
            <ts:snrDev imgSuffix="nimantha" desc="${descNimantha}" />
        </table>
        
        <br><br>

        <p class="bold">Past Team Members:</p>
        <div style="margin: 0 auto; padding: 0 52px 15px;">
            <ol>
                <c:set var="descBui">
                    <br>Bui Trong Nhan<br>
                    Area Lead - Scalability (Aug 2014 - Dec 2015)<br>
                    Snr Developer (June 2014 - Jul 2014)<br>
                    Committer (Apr 2014 - May 2014)<br><br>
                </c:set>
                <ts:pastMember imgSuffix="bui" desc="${descBui}" />
                <c:set var="descKai">
                    <br>Xie Kai<br>
                    Area Lead - Comments (Aug 2014 - Dec 2015)<br>
                    Snr Developer (June 2014 - Jul 2014)<br>
                    Committer (Apr 2014 - May 2014)<br><br>
                </c:set>
                <ts:pastMember imgSuffix="kai" desc="${descKai}" />
                <c:set var="descJunchao">
                    <br>Gu Junchao<br>
                    Area Lead - Access Control (Aug 2014 - Dec 2015)<br>
                    Snr Developer (June 2014 - Jul 2014)<br>
                    Committer (Apr 2014 - May 2014)<br><br>
                </c:set>
                <ts:pastMember imgSuffix="junchao" desc="${descJunchao}" />
                <ts:pastMember imgSuffix="shawn" desc="Shawn Lee (Area Lead, Snr Developer)" />
                <ts:pastMember imgSuffix="yangmo" desc="Yang Mo (Area Lead, Snr Developer)" />
                <ts:pastMember imgSuffix="arnold" desc="Arnold Koroa (Area Lead, Snr Developer)" />
                <ts:pastMember imgSuffix="ivan" desc="Ivan Poon (Area Lead, Snr Developer)" />
                <ts:pastMember imgSuffix="jamesdjuhartono" desc="James Djuhartono (Area Lead, Snr Developer)" />
                <ts:pastMember imgSuffix="ryan" desc="Ryan Chang (Snr Developer)" />
                <ts:pastMember imgSuffix="theongsiang" desc="Oo Theong Siang (Area Lead, Snr Developer)" />
                <ts:pastMember imgSuffix="hunar" desc="Hunar Khanna (Area Lead, Snr Developer)" />
                <ts:pastMember imgSuffix="qiaoliang" desc="Qiao Liang (Area Lead, Snr Developer)" />
                <ts:pastMember imgSuffix="kenny" desc="Tan How Cheng Kenny (Area Lead, Snr Developer)" />
                <ts:pastMember imgSuffix="chunteck" desc="Goh Chun Teck (Project Lead, Area Lead, Snr Developer)" />
                <ts:pastMember imgSuffix="wangsha" desc="Wang Sha (Project Lead, Area Lead, Snr Developer)" />
                <ts:pastMember imgSuffix="james" desc="Long Dam Tuan, James (Project Lead, Area Lead, Snr Developer)" />
                <br>
                <ts:pastMember desc="Aldrian Obaja (Area Lead, Snr Developer)" />
                <ts:pastMember desc="Kalpit Jain (Area Lead, Snr Developer)" />
                <ts:pastMember desc="Shakthi Priya Kathirvelu (Area Lead, Snr Developer)" />
                <ts:pastMember desc="Zhang Xialin (Area Lead, Snr Developer)" />
                <ts:pastMember desc="Huy Nguyen (Area Lead, Snr Developer)" />
                <ts:pastMember desc="Gerald Goh (Project Lead, Snr Developer)" />
                <ts:pastMember desc="Xiaoni Lai (Project Lead, Snr Developer)" />
            </ol>
        </div>
                
        <p class="bold">Contributors:</p>
        <br>
        <div style="margin: 0 auto; padding: 0 52px 15px;">
            <ol>
                <div style="margin: 0 auto; padding: 0 0 15px;">[Multiple contributions]</div>
                <ts:contributor name="Ang Ji Kai" />
                <ts:contributor name="Ang Jin Juan Gabriel" contribution="initial adoption of Selenium" />
                <ts:contributor name="Ang Nickolas" />
                <ts:contributor name="Anoop Shukla" ghUsername="weaveranoop" />
                <ts:contributor name="Anubhav Shrivastava" />
                <ts:contributor name="Arvind Kalyan" ghUsername="arvind-kalyan" />
                <ts:contributor name="Atishay Jain" ghUsername="atishay100495" />
                <ts:contributor name="Chen Shenglong Bryan" contribution="initial adoption of Selenium" />
                <ts:contributor name="Chen Di" ghUsername="sundychenD" />
                <ts:contributor name="Chen Zhuo" ghUsername="chen-zhuo" />
                <ts:contributor name="Cheung Yew Kong Benjamin" />
                <ts:contributor name="Chi Cheng" ghUsername="Chi-Cheng-Leon" />
                <ts:contributor name="Chin Yong Wei" />
                <ts:contributor name="Chong Kok Wei" />
                <ts:contributor name="Chow Yuan Bin" ghUsername="chowyb" />
                <ts:contributor name="Chua Hong Jing" />
                <ts:contributor name="Chua Jie Sheng" />
                <ts:contributor name="Chua Peng Chin Benson" contribution="initial adoption of Selenium" />
                <ts:contributor name="Daryl Lim" contribution="improved help documentation" />
                <ts:contributor name="Devang Gaur" ghUsername="drunken-saint" />
                <ts:contributor name="Dinesh Thangavel" />
                <ts:contributor name="Divya Pandilla" ghUsername="pandilladivya" />
                <ts:contributor name="Dong Yanfei" />
                <ts:contributor name="Du Lingyi" />
                <ts:contributor name="Foo Yong Jie" />
                <ts:contributor name="Gautam Dudeja" ghUsername="mt10589" />
                <ts:contributor name="Guan Xiaokang" />
                <ts:contributor name="Han Zijian" ghUsername="ihenria" />
                <ts:contributor name="Hou Guochen" />
                <ts:contributor name="Huang Guanyang Eugene" />
                <ts:contributor name="Huynh Van Quang" />
                <ts:contributor name="Jiang Sheng" ghUsername="Gisonrg" />
                <ts:contributor name="Jiang Yaoxuan" />
                <ts:contributor name="John Kevin Tjahjadi" ghUsername="jkt001" />
                <ts:contributor name="Karan Kamath" />
                <ts:contributor name="Karandeep Singh Bhatia" ghUsername="karan173" />
                <ts:contributor name="Kenneth Ho Chee Chong" ghUsername="kennho" />
                <ts:contributor name="Khoo Yong Jie" ghUsername="YongJieYongJie" />
                <ts:contributor name="Kseniia Solodovnik" />
                <ts:contributor name="Lakmal Padmakumara" ghUsername="rclakmal" />
                <ts:contributor name="Le Minh Khue" />
                <ts:contributor name="Le Viet Tien" />
                <ts:contributor name="Lian Wenhui Florine" contribution="initial adoption of Selenium" />
                <ts:contributor name="Liu Yuanrui" />
                <ts:contributor name="Loke Yan Hao" />
                <ts:contributor name="Luk Ming Kit" />
                <ts:contributor name="Michael Limantara" ghUsername="mikelimantara" />
                <ts:contributor name="Mohit Singh Kanwal" contribution="initial scalability experiments" />
                <ts:contributor name="Ng Joon Kiat Steve" />
                <ts:contributor name="Ng Zhi An" ghUsername="ngzhian" />
                <ts:contributor name="Nguyen Quang Phuc" />
                <ts:contributor name="Nguyen Truong Duy" />
                <ts:contributor name="Nisala Niroshana" ghUsername="NisalaNiroshana" />
                <ts:contributor name="Pak Chong Da Glen" />
                <ts:contributor name="Peh Shao Hong" />
                <ts:contributor name="Pham Minh Viet, Victor" />
                <ts:contributor name="Phan Thi Quynh Trang" />
                <ts:contributor name="Pon Heng Wai" />
                <ts:contributor name="Qiao Zhengyuan" />
                <ts:contributor name="Qua Zi Xian" ghUsername="ZiXian92" />
                <ts:contributor name="Ratul Ghosh" ghUsername="RatulGhosh" />
                <ts:contributor name="Richard Goulter" />
                <ts:contributor name="Ruchir Sharma" ghUsername="ruchirsharma1993" />
                <ts:contributor name="Samat Davletshin" ghUsername="samatdav" />
                <ts:contributor name="Serhii Beliablia" ghUsername="belyabl9" />
                <ts:contributor name="Shawn Teo Chee Yong" />
                <ts:contributor name="Shiluka Dharmasena" ghUsername="shiluka" />
                <ts:contributor name="Shum Chee How" />
                <ts:contributor name="Siddhant Gupta" ghUsername="Siddhant085" />
                <ts:contributor name="Sim Shengming, Eugene" />
                <ts:contributor name="Song Qiyue" />
                <ts:contributor name="Sreshta Vijayaraghavan" />
                <ts:contributor name="Staniloiu Eduard" />
                <ts:contributor name="Su Sumei" ghUsername="Sumei1009" />
                <ts:contributor name="Subhodip Kumar" ghUsername="subhodip7" />
                <ts:contributor name="Sudarsan Gopalaswami Padmanabhan" />
                <ts:contributor name="Sujeet" ghUsername="sujeet14108" />
                <ts:contributor name="Supraja Bhavani Sekhar" />
                <ts:contributor name="Svitlana Nesterenko" ghUsername="SvetaNesterenko" />
                <ts:contributor name="Tan Guo Wei" />
                <ts:contributor name="Tan Wee Kwan" />
                <ts:contributor name="Tan Wei Xin" />
                <ts:contributor name="Tan Xue Si" ghUsername="xeia" />
                <ts:contributor name="Tang Ning" ghUsername="github-tn" />
                <ts:contributor name="Tang Zijian" ghUsername="tangzijian" />
                <ts:contributor name="Tarun Kumar Yadav" ghUsername="tarun14110" />
                <ts:contributor name="Teo Yock Swee Terence" />
                <ts:contributor name="Thng Kai Yuan" ghUsername="thngkaiyuan" />
                <ts:contributor name="Truong Hoang Phuoc" />
                <ts:contributor name="Wong Yu Xuan" ghUsername="unyoungwax" />
                <ts:contributor name="Wang Chao" />
                <ts:contributor name="Winson Tanputraman" />
                <ts:contributor name="Wu Lifu" />
                <ts:contributor name="Xia Lu" />
                <ts:contributor name="Y V S S Santosh" ghUsername="yvsssantosh" />
                <ts:contributor name="Yasiru Nilan" ghUsername="yasirunilan" />
                <ts:contributor name="Yen Zi Shyun" />
                <ts:contributor name="Yogen Aggarwal" />
                <ts:contributor name="Zhang Haoqiang" />
                <ts:contributor name="Zheng Pingxia" />
                <ts:contributor name="Zhu Liang" ghUsername="paradite" />
                <br>
                <div style="margin: 0 auto; padding: 0 0 15px;">[One-time contributions]</div>
                <ts:contributor name="Alexandr Kolymago" ghUsername="justtimki" />
                <ts:contributor name="Angie Jones" ghUsername="angiejones" />
                <ts:contributor name="Atheesan Sornalingam" />
                <ts:contributor name="Avijit Gupta" ghUsername="526avijitgupta" />
                <ts:contributor name="Bruno Mendes" ghUsername="brnomendes" />
                <ts:contributor name="Chan Junwei" ghUsername="chanjunweimy" />
                <ts:contributor name="Chao Song" ghUsername="songchoa" />
                <ts:contributor name="Dustin Lapierre" ghUsername="dustinlapierre" />
                <ts:contributor name="Gable Heng" ghUsername="gableh" />
                <ts:contributor name="Harshvardhan Singh" ghUsername="Harshvardhan58" />
                <ts:contributor name="Hoo De Lin" ghUsername="HooDeLin" />
                <ts:contributor name="Jayant Jain" />
                <ts:contributor name="Jonathan Lim" ghUsername="jonathanlimsc" />
                <ts:contributor name="Justin Ouyang" />
                <ts:contributor name="Leonard Hio" ghUsername="leonardhml" />
                <ts:contributor name="Mairieli Wessel" ghUsername="mairieli" />
                <ts:contributor name="Malinda Kumarasinghe" />
                <ts:contributor name="Manvi Jain" ghUsername="manvijain" />
                <ts:contributor name="Marianna Sternefeld" ghUsername="mesternefeld" />
                <ts:contributor name="Maria Sam" ghUsername="mariasam1" />
                <ts:contributor name="Nguyen Khac Tung" ghUsername="tungnk1993" />
                <ts:contributor name="Niklas Wuensche" ghUsername="NWuensche" />
                <ts:contributor name="Nilesh Suthar" />
                <ts:contributor name="Onkar Shedge" ghUsername="onkarshedge" />
                <ts:contributor name="Paramsothy Sanshayan" />
                <ts:contributor name="Prithviraj Billa" />
                <ts:contributor name="P S Narayanan" ghUsername="PSNAppz" />
                <ts:contributor name="Pulasthi Harasgama" ghUsername="Pulasthih" />
                <ts:contributor name="Rajat Saxena" ghUsername="srajat" />
                <ts:contributor name="Rajiv Jha" ghUsername="rajivjhaimin" />
                <ts:contributor name="Rohith Mukku" ghUsername="Naruto8" />
                <ts:contributor name="Shiv Kandikuppa" ghUsername="shiv12095" />
                <ts:contributor name="Sri Aurobindo Munagala" ghUsername="Einsteino" />
                <ts:contributor name="Steven Khong" />
                <ts:contributor name="Tarun Bansal" />
                <ts:contributor name="Ujjwal Wahi" ghUsername="ujjwalwahi" />
                <ts:contributor name="Vaibhav Desai" />
                <ts:contributor name="Venkatesan Harish" ghUsername="harishv7" />
            </ol>
            <br>
            <p class="bold">
                Would you like to join the TEAMMATES team as a contributor? <a href="${contactPage}">Contact Us</a>.
            </p>
        </div>
    </div>
</t:staticPage>