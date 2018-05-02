:: Configuration du script

SET REPOSITORY=git@github.com:ClementSalvador/projetTest.git
::SET REPOSITORY = https://github.com/ClementSalvador/projetTest.git
SET VERSION_FROM=Rel_01
SET VERSION_TO=Dev
SET REPORT_FOLDER=C:/Users/clement.salvador/Documents/GitVC/rapport
set REPORT_DATE=19920627


::SET REPOSITORY=git@git-dev.server.lan:THULLIEZ/formation-git.git
::SET VERSION_FROM=master
::SET VERSION_TO=alpha
::SET REPORT_FOLDER=C:\user\THULLIEZ\WORKBOX\Workspace\GitVC\rapport
::set REPORT_DATE=180221143443612

:: Fin de configuration du script

echo Lancement de la comparaison de la version %VERSION_FROM% et %VERSION_TO% 
:: On se rend dans le dossier tmp de windows
::cd C:\Windows\Temp

:: Récupération du nom du dossier qui sera creee par le clone
for /f "tokens=1,2 delims=/ " %%a in ("%REPOSITORY%") do set draft=%%a&set REPO_FOLDER=%%b
for /f "tokens=1,2 delims=. " %%a in ("%REPO_FOLDER%") do set REPO_FOLDER=%%a&set draft=%%b

SET REPO_FOLDER=%REPO_FOLDER%

:: Generation du nom de fichier rapport

::set REPORT_FILE_PATH=%REPORT_FOLDER%/%REPO_FOLDER%/%REPORT_DATE%/diff.log
set REPORT_FILE_PATH=C:/Users/clement.salvador/Documents/GitVC/diff.log
set REPORT_FOLDER=%REPORT_FOLDER%/%REPO_FOLDER%/%REPORT_DATE%
SET MERGED_FOLDER=%REPORT_FOLDER%/merged_%VERSION_FROM%
SET VERSION_TO_FOLDER=%REPORT_FOLDER%/%VERSION_TO%
SET REPORT_FOLDER_LOG=%REPORT_FOLDER%/stats

:: Creation des dossiers utilises dans le cadre du rapport
mkdir "%REPORT_FOLDER%"
mkdir "%MERGED_FOLDER%"
mkdir "%VERSION_TO_FOLDER%"
mkdir "%REPORT_FOLDER_LOG%"

:: On recupere le contenu depuis le repository 
git clone https://github.com/ClementSalvador/projetTest.git

::
CALL :CHECK_FAIL

:: On se met dans le dossier ainsi cree
cd %REPO_FOLDER%


:: On fait le merge et le diff
git checkout %VERSION_FROM%

CALL :CHECK_FAIL

git branch tmp_%VERSION_FROM%

git checkout tmp_%VERSION_FROM%

CALL :CHECK_FAIL

git merge remotes/origin/%VERSION_TO%

CALL :CHECK_FAIL

git diff --name-only tmp_%VERSION_FROM%..remotes/origin/%VERSION_TO% > %REPORT_FILE_PATH%

CALL :CHECK_FAIL

::En fonction de ce qu'on a récupéré, on va aller chercher les fichiers qui nous intéressent pour la branche source
for /f "usebackq delims=" %%a in (%REPORT_FILE_PATH%) do echo F|xcopy /S /Q /Y /F "%%a" "%MERGED_FOLDER%/%%a"

git checkout %VERSION_TO%

::En fonction de ce qu'on a récuèré, on va aller chercher les fichiers qui nous intéressent pour la branche cible
for /f "usebackq delims=" %%a in (%REPORT_FILE_PATH%) do echo F|xcopy /S /Q /Y /F "%%a" "%VERSION_TO_FOLDER%/%%a"

:: On se remet sur la version n-1 et on va faire un log sur les fichiers, et stocker le resultat pour comprendre d'ou viennent les modifs
git checkout %VERSION_FROM%
mkdir "%REPORT_FOLDER_LOG%/tmp"

For /f "usebackq delims=" %%A in (%REPORT_FILE_PATH%) do ( 
	::echo mkdir "%REPORT_FOLDER_LOG%/tmp/%%A"
	mkdir "%REPORT_FOLDER_LOG%/tmp/ola"
	git log --full-diff -p %%A > "%REPORT_FOLDER_LOG%/tmp/ola/gitlog.log"
	git blame %%A > "%REPORT_FOLDER_LOG%/tmp/ola/gitblame.log"
	git diff remotes/origin/%VERSION_TO%..tmp_%VERSION_FROM% %%A > "%REPORT_FOLDER_LOG%/tmp/ola/gitdiff.log"
	move "%REPORT_FOLDER_LOG%/tmp/ola" "%REPORT_FOLDER_LOG%"
)


rd /s /q "%REPORT_FOLDER_LOG%/tmp"


cd ..
rd /s /q %REPO_FOLDER%

echo Fin de la comparaison de la version %VERSION_FROM% et %VERSION_TO%. Le resultat se trouve dans %REPORT_FILE_PATH%




:CHECK_FAIL
if NOT ["%errorlevel%"]==["0"] (
	cd ..
	:rd /s /q %REPO_FOLDER%
	exit
    
)
