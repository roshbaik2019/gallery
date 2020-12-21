<?php
require __DIR__ . '/../vendor/autoload.php';
use \Psr\Http\Message\ServerRequestInterface as Request;
use \Psr\Http\Message\ResponseInterface as Response;
use Slim\Http\UploadedFile;
require __DIR__ . '/../includes/DbOperations.php';
require __DIR__ . '/../libs/gcm/firebase.php';
require __DIR__ . '/../libs/gcm/push.php';

$app = new \Slim\App([
    'settings'=>[
        'displayErrorDetails'=>true
    ]
]);

$app->add(new Tuupola\Middleware\HttpBasicAuthentication([
    "secure"=>false,
    "users" => [
        "roshbaik" => "123456",
       
    ]
]));


$container = $app->getContainer();
$container['upload_directory'] = __DIR__ . '/uploads';

$user_id = NULL;

// الحصول على جميع الصور
$app->get('/allphoto', function(Request $request, Response $response,array $args){

    $db = new DbOperations; 

    $photo = $db->getAllPhoto();
    $response_data = array();

    $response_data['error'] = false; 
    $response_data['photo'] = $photo; 

    $response->write(json_encode($response_data));

    return $response
    ->withHeader('Content-type', 'application/json')
    ->withStatus(200);  

});

// الحصول على معلومات الصورة بواسطة الايدي
$app->get('/photoinfobyid/{id}',function(Request $request, Response $response,array $args){
    $id = $args['id'];
    $db = new DbOperations; 

    $photo_Info = $db->getPhotoInfoById($id);
    if ($db->updateView($id, ($photo_Info[0]['views']) + 1)) {

        $response_data = array();

        $response_data['error'] = false; 
        $response_data['photo_info'] = $photo_Info; 

        $response->write(json_encode($response_data));

    return $response
    ->withHeader('Content-type', 'application/json')
    ->withStatus(200);
    
    }

});

// تحديث عدد المشاهدات للصورة
$app->put('/saveview', function(Request $request, Response $response) {
    if(!haveEmptyParameters(array('id', 'views'), $request, $response)){
        $request_data = $request->getParsedBody(); 
        
        $id = $request_data['id'];
        $views = $request_data['views'];
        $db = new DbOperations; 
        $response_data = array();

        if ($db->updateView($id, $views)) {
            $response_data['error'] = false;
            $response_data['message'] = "views stored";
            $response->write(json_encode($response_data));

            return $response
                ->withHeader('Content-type', 'application/json')
                ->withStatus(200);   
        } else {
            $response_data['error'] = true;
            $response_data['message'] = "Could not store views";
            $response->write(json_encode($response_data));

            return $response
                ->withHeader('Content-type', 'application/json')
                ->withStatus(200);   
        }
        

    }

    return $response
    ->withHeader('Content-type', 'application/json')
    ->withStatus(422); 
    
});

// الحصول على جميع الاقسام
$app->get('/allsection', function(Request $request, Response $response,array $args){

    $db = new DbOperations; 
    $section = $db->getAllSection();
    $response_data = array();

    $response_data['error'] = false; 
    $response_data['section'] = $section;

    $response->write(json_encode($response_data));

    return $response
    ->withHeader('Content-type', 'application/json')
    ->withStatus(200);  

});

// الحصول على جميع الصور داخل قسم ما
$app->get('/photoinsection/{id}',function(Request $request, Response $response,array $args){
    $id = $args['id'];
    $db = new DbOperations;

    $photo_Info = $db->getAllPhotoSection($id);
   

        $response_data = array();

        $response_data['error'] = false; 
        $response_data['photo_info'] = $photo_Info; 

        $response->write(json_encode($response_data));

    return $response
    ->withHeader('Content-type', 'application/json')
    ->withStatus(200);
    

});

// الحصول على عدد النقاط الحالي

$app->get('/getmyearn/{id}',function(Request $request, Response $response,array $args){
    $id = $args['id'];
    $db = new DbOperations; 

    $myearn = $db->getmyearn($id);

    $response_data = array();

    $response_data['error'] = false; 
    $response_data['message'] = $myearn; 

    $response->write(json_encode($response_data));

    return $response
    ->withHeader('Content-type', 'application/json')
    ->withStatus(200);  

});

// upload Profile Image
// البراميترات
// id / imageType = imagethumb or imagecover
    
$app->post('/Upload', function(Request $request, Response $response) {

    $directory = $this->get('upload_directory');

    if(!is_dir($directory)){
        mkdir($directory);
        chmod($directory, 0777);
    }
   
    
   if(!haveEmptyParameters(array('sectionName', 'imageType'), $request, $response)){

        $uploadedFiles = $request->getUploadedFiles(); // ???? ?????? ??? ??? ?????? ??????? 
        // handle single input with single file upload
        $filename = $uploadedFiles['profileimage']; // ????? ????? ?????? ?????


       if ($filename->getError() === UPLOAD_ERR_OK) {
           $request_data = $request->getParsedBody(); 

           $sectionName_id = $request_data['sectionName'];
           $imageType = $request_data['imageType'];

          //$response->write('sectionName =  ' . $sectionName_id . '<br/>');
          //$response->write('imageType = ' . $imageType . '<br/>');

        //$newfilename = moveToUploadedFiletemp($directory, $filename);
        //$response->write('uploaded ' . $newfilename . '<br/>');

            $imageName = moveToUploadedFile($directory, $filename,$sectionName_id,$imageType);
            //$response->write('uploaded ' . $imageName . '<br/>');

		$imageName = "https://" . gethostname() . "/surhds/public/uploads/" . $imageName;

            // update profile image to db
            updateProfileImageTodb($sectionName_id,$imageName,$imageType ,$request, $response);
		}


        
    }

});
/**
 * Moves the uploaded file to the upload directory and assigns it a unique name
 * to avoid overwriting an existing uploaded file.
 *
 * @param string $directory directory to which the file is moved
 * @param UploadedFile $uploadedFile file uploaded file to move
 * @return string filename of moved file
 */

function moveToUploadedFile($directory, UploadedFile $filename,$sectionName_id,$imageType)
{
    $extension = pathinfo($filename->getClientFilename(), PATHINFO_EXTENSION);
   // $basename = bin2hex(random_bytes(8)); // see http://php.net/manual/en/function.random-bytes.php
    $basename = $imageType . "_" . date("h:i:sa");
    $file = sprintf('%s.%0.8s', $basename, $extension);

    $userfolder = $directory . "/" ;
    if(!is_dir($userfolder)){
        mkdir($userfolder);
        chmod($userfolder, 0777);
    }
   
    //$directory = $directory . "/" . $id;

    $filename->moveTo($userfolder . DIRECTORY_SEPARATOR . $file);

    return $file;

}

function updateProfileImageTodb($sectionName_id,$imageName,$imageType,$request,$response)
{


    $db = new DbOperations;  

        $result = $db->updateprofileimage($sectionName_id,$imageName,$imageType);
        
        if($result == IMAGE_CREATED){

            $message = array(); 
            $message['error'] = false; 
            $message['message'] = 'profile created successfully';

            $response->write(json_encode($message));

             return $response
                        ->withHeader('Content-type', 'application/json')
                        ->withStatus(201);

        }else if($result == IMAGE_FAILURE){

            $message = array(); 
            $message['error'] = true; 
            $message['message'] = 'Some error occurred';

            $response->write(json_encode($message));

            return $response
                        ->withHeader('Content-type', 'application/json')
                       ->withStatus(422);    

        }else if($result == IMAGE_EXISTS){
            $message = array(); 
            $message['error'] = true; 
            $message['message'] = 'image Already Exists';

            $response->write(json_encode($message));

            return $response
                        ->withHeader('Content-type', 'application/json')
                        ->withStatus(422);    
        }
}

function haveEmptyParameters($required_params, $request, $response){
    $error = false; 
    $error_params = '';
    $request_params = $request->getParsedBody(); 

    foreach($required_params as $param){
        if(!isset($request_params[$param]) || strlen($request_params[$param])<=0){
            $error = true; 
            $error_params .= $param . ', ';
        }
    }

    if($error){
        $error_detail = array();
        $error_detail['error'] = true; 
        $error_detail['message'] = 'Required parameters ' . substr($error_params, 0, -2) . ' are missing or empty';
        $response->write(json_encode($error_detail));
    }
    return $error; 
}

$app->get('/allads', function(Request $request, Response $response,array $args){

    $db = new DbOperations; 

    $ads = $db->getAllAds();
    $response_data = array();

    $response_data['error'] = false; 
    $response_data['ads'] = $ads; 

    $response->write(json_encode($response_data));

    return $response
    ->withHeader('Content-type', 'application/json')
    ->withStatus(200);  

});

//للاعلان تحديث عدد المشاهداتة
$app->put('/saveviewads', function(Request $request, Response $response) {
    if(!haveEmptyParameters(array('id', 'views'), $request, $response)){
        $request_data = $request->getParsedBody(); 
        
        $id = $request_data['id'];
        $views = $request_data['views'];
        $db = new DbOperations; 
        $response_data = array();

        if ($db->updateViewAds($id, $views)) {
            $response_data['error'] = false;
            $response_data['message'] = "views stored";
            $response->write(json_encode($response_data));

            return $response
                ->withHeader('Content-type', 'application/json')
                ->withStatus(200);   
        } else {
            $response_data['error'] = true;
            $response_data['message'] = "Could not store views";
            $response->write(json_encode($response_data));

            return $response
                ->withHeader('Content-type', 'application/json')
                ->withStatus(200);   
        }
        

    }

    return $response
    ->withHeader('Content-type', 'application/json')
    ->withStatus(422); 
    
});

$app->put('/saveclickads', function(Request $request, Response $response) {
    if(!haveEmptyParameters(array('id', 'click'), $request, $response)){
        $request_data = $request->getParsedBody(); 
        
        $id = $request_data['id'];
        $click = $request_data['click'];
        $db = new DbOperations; 
        $response_data = array();

        if ($db->updateClickAds($id, $click)) {
            $response_data['error'] = false;
            $response_data['message'] = "click stored";
            $response->write(json_encode($response_data));

            return $response
                ->withHeader('Content-type', 'application/json')
                ->withStatus(200);   
        } else {
            $response_data['error'] = true;
            $response_data['message'] = "Could not store click";
            $response->write(json_encode($response_data));

            return $response
                ->withHeader('Content-type', 'application/json')
                ->withStatus(200);   
        }
        

    }

    return $response
    ->withHeader('Content-type', 'application/json')
    ->withStatus(422); 
    
});

$app->run();

