<?php
// ビルド時にバージョンを自動で設定する
date_default_timezone_set("Asia/Tokyo");
define("YmlLocation", __DIR__ . "/src/main/resources/plugin.yml");
if (file_exists(__DIR__ . "/src/main/resources/COMMIT")) {
    $COMMIT = "_" . mb_substr(file_get_contents(__DIR__ . "/src/main/resources/COMMIT"), 0, 7);
} else {
    $COMMIT = "";
}
define("Version", date("Y.m.d_H.i") . $COMMIT);

if(!file_exists(YmlLocation)){
    echo "Yml not found.\n";
    exit;
}
$data = file_get_contents(YmlLocation);
if(preg_match("/version: (.+)/", $data, $m) == false){
    echo "Version row not found.\n";
    exit;
}
$data = str_replace($m[0], "version: " . Version, $data);

file_put_contents(YmlLocation, $data);

echo "Inserted version: " . Version . "\n";