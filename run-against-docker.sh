
echo "If not already running, start Estatio database on Docker using:"
echo ""
echo "./sqlserver-container.sh -rlfau"
echo ""

mvnd -o -pl cfct-webapp -am spring-boot:run \
  -Dspring-boot.run.main-class=com.danhaywood.cfct.webapp.CfctWebApplication \
  -Dspring-boot.run.profiles=sqlhost_docker \
  -Dspring-boot.run.jvmArguments="-Xss2048k -Xms1024m -Xmx3096m"
