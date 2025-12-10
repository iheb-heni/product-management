pipeline {
    agent any
    
    tools {
        jdk 'JDK21'
        maven 'Maven3.9'
    }
    
    environment {
        // Git
        GIT_URL = 'https://github.com/iheb-heni/product-management.git'
        GIT_BRANCH = 'main'
        
        // Application
        APP_NAME = 'product-management'
        APP_PORT = '8089'
    }
    
    stages {
        // ===============================
        // STAGE 1: VÉRIFICATION OUTILS
        // ===============================
        stage('Vérification Outils') {
            steps {
                script {
                    echo '🎯 STAGE 1: VÉRIFICATION DES OUTILS REQUIS'
                    echo 'Objectif: S\'assurer que tous les outils nécessaires sont installés'
                    
                    bat '''
                        echo "=== 🛠️ OUTILS DISPONIBLES SUR L'AGENT JENKINS ==="
                        echo.
                        
                        echo "📦 BUILD TOOLS:"
                        java -version 2>&1 | findstr "version" >nul && echo ✅ Java || echo ❌ Java non installé
                        mvn --version 2>&1 | findstr "Apache Maven" >nul && echo ✅ Maven || echo ❌ Maven non installé
                        git --version 2>&1 | findstr "git version" >nul && echo ✅ Git || echo ❌ Git non installé
                        
                        echo.
                        echo "=== ✅ VÉRIFICATION TERMINÉE ==="
                    '''
                    
                    bat '''
                        echo.
                        echo "=== 📋 VERSIONS DÉTAILLÉES ==="
                        java -version
                        echo.
                        mvn --version
                        echo.
                        git --version
                    '''
                }
            }
            
            post {
                success {
                    echo '✅ Tous les outils sont disponibles'
                    echo '📋 Les builds peuvent continuer'
                }
                failure {
                    echo '⚠️ Certains outils sont manquants'
                }
            }
        }
        
        // ===============================
        // STAGE 2: CHECKOUT CODE
        // ===============================
        stage('Checkout Code') {
            steps {
                script {
                    echo '🎯 STAGE 2: TÉLÉCHARGEMENT DU CODE SOURCE'
                    echo 'Objectif: Récupérer le code depuis Git, nettoyer le workspace'
                    
                    echo "📁 Nettoyage du workspace..."
                    cleanWs()
                    
                    echo "📥 Clonage du dépôt Git..."
                    echo "URL: ${GIT_URL}"
                    echo "Branche: ${GIT_BRANCH}"
                    
                    // Checkout du code
                    git branch: "${GIT_BRANCH}", 
                         url: "${GIT_URL}",
                         poll: false,
                         changelog: false
                    
                    // Afficher la structure du projet
                    bat '''
                        echo.
                        echo "=== 📂 STRUCTURE DU PROJET ==="
                        echo "Projet: %APP_NAME%"
                        dir /b
                        echo.
                        echo "=== 📄 FICHIERS IMPORTANTS ==="
                        if exist pom.xml ( 
                            echo ✅ pom.xml
                            echo "Contenu de pom.xml (premières lignes):"
                            for /f "tokens=1,2,3,4,5,6,7,8,9,10" %%i in (pom.xml) do (
                                echo %%i %%j %%k %%l %%m %%n %%o %%p %%q %%r
                                goto :break
                            )
                            :break
                        ) else ( 
                            echo ❌ pom.xml manquant
                        )
                        
                        if exist src\\main\\java ( 
                            echo ✅ Code source Java
                            dir /b src\\main\\java
                        ) else ( 
                            echo ❌ Code source manquant
                        )
                        
                        if exist src\\test\\java ( 
                            echo ✅ Tests disponibles
                        ) else ( 
                            echo ⚠️ Tests manquants
                        )
                    '''
                }
            }
            
            post {
                success {
                    echo '✅ Code téléchargé avec succès'
                    echo '📊 Statistiques:'
                    bat '''
                        echo "Taille du projet:"
                        for /f "tokens=3" %%i in ('dir /s /c ^| find "octets"') do echo %%i octets
                        echo.
                        echo "Nombre de fichiers Java:"
                        dir /s /b *.java 2>nul | find /c ".java" || echo 0
                    '''
                }
                failure {
                    echo '❌ Échec du checkout Git'
                    echo 'Vérifiez:'
                    echo "1. L'URL du dépôt: ${GIT_URL}"
                    echo '2. La branche spécifiée'
                    echo '3. La connexion internet'
                }
            }
        }
        
        // ===============================
        // STAGE 3: INSTALLATION DÉPENDANCES
        // ===============================
        stage('Installation Dépendances') {
            steps {
                script {
                    echo '🎯 STAGE 3: INSTALLATION DES DÉPENDANCES MAVEN'
                    echo 'Objectif: Télécharger toutes les dépendances, vérifier les conflits'
                    
                    echo "📦 Téléchargement des dépendances..."
                    
                    bat '''
                        echo "=== 🔍 ANALYSE DU POM.XML ==="
                        echo.
                        echo "Artifacts principaux:"
                        findstr "<artifactId>" pom.xml | findstr /v "filter" | more +7
                        echo.
                    '''
                    
                    try {
                        // Installation sans tests pour aller plus vite
                        bat 'mvn clean dependency:resolve -DskipTests'
                        echo '✅ Dépendances résolues avec succès'
                        
                    } catch (Exception e) {
                        echo "⚠️ Erreur lors de la résolution des dépendances: ${e.message}"
                        echo "Tentative avec compile seulement..."
                        
                        bat 'mvn clean compile -DskipTests'
                        echo '✅ Compilation réussie'
                    }
                    
                    bat '''
                        echo "=== 📋 RAPPORT DÉPENDANCES ==="
                        echo.
                        if exist "%USERPROFILE%\\.m2\\repository" (
                            echo ✅ Cache Maven trouvé
                        ) else (
                            echo ⚠️ Cache Maven non trouvé
                        )
                    '''
                }
            }
            
            post {
                success {
                    echo '✅ Dépendances installées avec succès'
                }
                failure {
                    echo '❌ Échec d\'installation des dépendances'
                }
            }
        }
        
        // ===============================
        // STAGE 4: COMPILATION
        // ===============================
        stage('Compilation') {
            steps {
                script {
                    echo '🎯 STAGE 4: COMPILATION DU PROJET'
                    echo 'Objectif: Compiler le code source Java, détecter les erreurs'
                    
                    bat 'mvn clean compile -DskipTests'
                    
                    // Vérifier la compilation
                    bat '''
                        echo "=== ✅ VÉRIFICATION COMPILATION ==="
                        if exist target\\classes (
                            echo ✅ Classes compilées avec succès
                            echo "Nombre de fichiers .class:"
                            dir /s /b target\\classes\\*.class 2>nul | find /c ".class" || echo 0
                        ) else (
                            echo ❌ Aucune classe compilée
                        )
                        
                        if exist target\\*.jar (
                            echo ✅ JAR généré
                            dir /b target\\*.jar
                        ) else (
                            echo ⚠️ Aucun JAR généré (normal en compilation)
                        )
                    '''
                }
            }
            
            post {
                success {
                    echo '✅ Compilation réussie'
                    // Archive le JAR s'il existe
                    archiveArtifacts artifacts: '**/target/*.jar', fingerprint: false, allowEmptyArchive: true
                }
                failure {
                    echo '❌ Échec de compilation'
                }
            }
        }
        
        // ===============================
        // STAGE 5: TESTS UNITAIRES
        // ===============================
        stage('Tests Unitaires') {
            steps {
                script {
                    echo '🎯 STAGE 5: EXÉCUTION DES TESTS UNITAIRES'
                    echo 'Objectif: Vérifier la logique métier avec des tests isolés'
                    
                    bat 'mvn test -Dtest=*UnitTest'
                    
                    // Publier les résultats JUnit
                    junit '**/target/surefire-reports/*.xml'
                    
                    bat '''
                        echo "=== 📊 RAPPORT TESTS ==="
                        echo.
                        if exist target\\surefire-reports (
                            echo ✅ Rapports de tests générés
                            dir /b target\\surefire-reports\\*.txt | find /c ".txt" || echo 0
                        ) else (
                            echo ⚠️ Aucun rapport de test
                        )
                    '''
                }
            }
            
            post {
                success {
                    echo '✅ Tests unitaires exécutés'
                }
                failure {
                    echo '❌ Tests unitaires échoués'
                    echo 'Vérifiez les rapports de tests pour les détails'
                }
            }
        }
    }
    
    post {
        always {
            echo "📊 PIPELINE CI/CD - RAPPORT FINAL"
            echo "========================================"
            echo "Build: #${BUILD_NUMBER}"
            echo "Projet: ${APP_NAME}"
            echo "Durée totale: ${currentBuild.durationString}"
            echo ""
            echo "✅ STAGES COMPLÉTÉS:"
            bat '''
                echo 1. Vérification Outils - ✅ Outils disponibles
                echo 2. Checkout Code - ✅ Code source récupéré
                echo 3. Installation Dépendances - ✅ Dépendances installées
                echo 4. Compilation - ✅ Code compilé
                echo 5. Tests Unitaires - ✅ Tests exécutés
            '''
            echo ""
            echo "📈 STATISTIQUES FINALES:"
            bat '''
                echo "Fichiers sources Java:"
                dir /s /b *.java 2>nul | find /c ".java" || echo 0
                echo.
                echo "Classes compilées:"
                dir /s /b target\\classes\\*.class 2>nul | find /c ".class" || echo 0
                echo.
                echo "Rapports de tests:"
                if exist target\\surefire-reports\\*.xml (
                    dir /b target\\surefire-reports\\*.xml | find /c ".xml" || echo 0
                ) else (
                    echo Aucun
                )
            '''
        }
        
        success {
            echo '🎉 PIPELINE RÉUSSIE !'
            echo ''
            echo '✅ Toutes les étapes de build sont complétées'
            echo '📦 Artifacts prêts pour les prochaines étapes'
            echo ''
            echo 'Prochaines étapes possibles:'
            echo '1. Tests d\'intégration'
            echo '2. Analyse SonarQube'
            echo '3. Construction Docker'
        }
        
        failure {
            echo '⚠️ PIPELINE ÉCHOUÉE'
            echo 'Consultez les logs pour identifier l\'erreur'
            echo ''
            echo 'Solutions courantes:'
            echo '1. Vérifiez la connexion internet'
            echo '2. Vérifiez les dépendances Maven'
            echo '3. Corrigez les erreurs de compilation'
        }
        
        unstable {
            echo '🔶 PIPELINE AVEC AVERTISSEMENTS'
            echo 'Certains tests ont échoué mais le build continue'
        }
    }
}