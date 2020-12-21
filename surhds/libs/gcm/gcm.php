<?php

class GCM
{
    function __construct()
    {

    }

    //This function will send message to the given registration ids
    //We are also passing a message that is actually an array containing the message
    public function sendMessage($registration_ids, $message) {
        /*
        $fields = array(
            'registration_ids' => $registration_ids,
            'data' => $message,
        );
        */
       
        $fields = array(
            'to' => "dBN2al7oMxk:APA91bGitNgtaBO6BfAEgR4o3dHS6vlyi8cxgMeaeyL8H2oz7qJdA44apMJVY4T0LRuEUlHSeM__FXMAKnrXE1eDkRyIL3D8P4aqfyzB0Oq9uuKTBxK6idZ6BIifEXUkaY9Y0aZ0C-mM",
            'data' => "test",
        );


        //In this function we are calling the main method responsible for sending push notification
        //it is sendPushNotification
        return $this->sendPushNotification($fields);
    }

    //This is the main method responsible for sending push notification
    //I have already explained it in previous tutorials
    private function sendPushNotification($fields){
        include_once __DIR__ . '/../../include/Config.php';
        $data = json_encode($fields);
        $url = 'https://fcm.googleapis.com/fcm/send';

        //api_key in Firebase Console -> Project Settings -> CLOUD MESSAGING -> Server key
        $server_key = 'AAAA_YS22C0:APA91bGEB_CvrF2dPDSvj_Jy1LEyF1nDC7A7S5Yh2eWGche-J2xlUA_dBI6QlqLKUpbbLbemwwwvRYowTaugvCA7Kg0RgWqXUGqHy3d6vfpuWBglJOD_vS1Z4-VLxSoj0Fl6BF4a1NIN';

        $headers = array(
            'Authorization: key='.$server_key,
            'Content-Type: application/json'
        );

        $ch = curl_init();

        curl_setopt($ch, CURLOPT_URL, $url);
        curl_setopt($ch, CURLOPT_POST, true);
        curl_setopt($ch, CURLOPT_HTTPHEADER, $headers);
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
        
        curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, false);
        curl_setopt($ch, CURLOPT_POSTFIELDS, $data);

        $result = curl_exec($ch);
        if ($result === FALSE) {
            die('Oops! FCM Send Error: ' . curl_error($ch));
        }
        curl_close($ch);
        echo 'FCM.....';
        return $result;
    }
}