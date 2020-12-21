<?php 
 
    class DbOperations{

        private $con; 
    
        function __construct(){
            require_once dirname(__FILE__) . '/DbConnect.php';
            $db = new DbConnect; 
            $this->con = $db->connect();
            $sSQL= 'SET CHARACTER SET utf8';
		  $this->con->query($sSQL);

        }

        // جميع الصور
        public function getAllPhoto(){
            $stmt = $this->con->prepare("SELECT id, url,sectionName,views FROM photo");
            $stmt->execute();
            $stmt->bind_result($id, $url,$sectionName, $views);
            $Photo = array();
            while($stmt->fetch()){
                $pic = array();
                $pic['id'] = $id; 
                $pic['url']=$url; 
                $pic['sectionName'] = $sectionName;
                $pic['views'] = $views;
                array_push($Photo, $pic);
            }            
            return $Photo;
        }

        // الحصول على معلومات الصورة عن طريق id
        public function getPhotoInfoById($id){
            $query = "SELECT id, url,sectionName,views FROM photo WHERE id = $id;";
        if ($result = $this->con->query($query)) {
            $Photo = array();
             while ($row = $result->fetch_row()) {
                        $pic = array();
                        $pic['id']=$row[0]; 
                        $pic['url'] = $row[1];
                        $pic['sectionName'] = $row[2];
                        $pic['views'] = $row[3]; // نزود عدد المشاهدات بمقدار 1
                        array_push($Photo, $pic);
                }
            
            }
                return $Photo;
            
        }

         // تحديث عدد المشاهدات للصورة
          public function updateView($id, $total)
         {
			$total = $total + 1;
             $stmt = $this->con->prepare("UPDATE photo SET views =? WHERE id=?");
             $stmt->bind_param("ii", $total, $id);
             if ($stmt->execute())
                 return true;
             return false;
         }

        // جميع اقسام الصور

        public function getAllSection(){
            $stmt = $this->con->prepare("SELECT id, url,sectionName FROM photo_section");
            $stmt->execute();
            $stmt->bind_result($id, $url,$sectionName);
            $section = array();
            while($stmt->fetch()){
                $pic = array();
                $pic['id'] = $id; 
                $pic['url']=$url; 
                $pic['sectionName'] = $sectionName;
                array_push($section, $pic);
            }            
            return $section;
        }

        // جميع الصور فى القسم
        public function getAllPhotoSection($sectionID){
            $query = "SELECT id, url,sectionName,views FROM photo WHERE sectionName = $sectionID;";
            if ($result = $this->con->query($query)) {
                $Photo = array();
                 while ($row = $result->fetch_row()) {
                            $pic = array();
                            $pic['id']=$row[0]; 
                            $pic['url'] = $row[1];
                            $pic['sectionName'] = $row[2];
                            $pic['views'] = $row[3]; // نزود عدد المشاهدات بمقدار 1
                            array_push($Photo, $pic);
                    }
                
                }
                    return $Photo;

        }



         
        // الحصول على عدد النقاط الحالي
        public function getmyearn($id){

            $stmt = $this->con->prepare("SELECT pointearn FROM users WHERE  id = ?;");
            $stmt->bind_param("i",$id);
            $stmt->execute();

            $stmt->bind_result($pointearn);
            $stmt->fetch(); 
            return $pointearn;
        }

        // save user profile image to db
        public function updateprofileimage($sectionid,$url,$imageType){

          if($imageType = "imageuser"){
            $stmt = $this->con->prepare("INSERT INTO photo (sectionName,url) VALUES (?,?)");
            $stmt->bind_param("ss",$sectionid,$url);
            if($stmt->execute())
                return IMAGE_CREATED;
            return IMAGE_FAILURE;
          }

            return IMAGE_EXISTS; 

        }

        // الحصول علي الاعلانات للافليت
        public function getAllAds(){
            $stmt = $this->con->prepare("SELECT id, code,click,views FROM ads ORDER BY RAND() LIMIT 1");
            $stmt->execute();
            $stmt->bind_result($id, $code,$click,$views);
            $Ads = array();
            while($stmt->fetch()){
                $ad = array();
                $ad['id'] = $id; 
                $ad['code']=$code;
                $ad['click']=$click;
                $ad['views']=$views;
                array_push($Ads, $ad);
            }            
            return $Ads;
        }

         // تحديث عدد المشاهدات للاعلان
         public function updateViewAds($id, $total)
         {
			$total = $total + 1;
             $stmt = $this->con->prepare("UPDATE ads SET views =? WHERE id=?");
             $stmt->bind_param("ii", $total, $id);
             if ($stmt->execute())
                 return true;
             return false;
         }

          // تحديث عدد المشاهدات للاعلان
          public function updateClickAds($id, $total)
          {
             $total = $total + 1;
              $stmt = $this->con->prepare("UPDATE ads SET click =? WHERE id=?");
              $stmt->bind_param("ii", $total, $id);
              if ($stmt->execute())
                  return true;
              return false;
          }
 


    }