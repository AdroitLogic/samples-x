mvn -o clean
XS_IGNORE_PATTERNS=".xpos .odg .idea license.key.properties license.properties client.key.properties .iml .log" python build.py
aws s3 cp --acl public-read target/ s3://downloads.x.adroitlogic.com/ultrastudio/19_05/samples/