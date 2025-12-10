pipeline {
    agent any
    
    tools {
        jdk 'JDK21'
        maven 'Maven3.9'
    }
    
    environment {
        // Git - METTEZ VOTRE VRAI URL GITHUB
        GIT_URL = 'https://github.com/Emrane23/product-management.git'  // CHANGEZ-MOI !
        GIT_BRANCH = 'main'
        
        // Application
        APP_NAME = 'product-management'
        APP_PORT = '8089'
        
        // SonarQube (optionnel pour l'instant)
        SONAR_HOST_URL = 'http://localhost:9000'
        SONAR_PROJECT_KEY = 'product-management-api'
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
                    
                    // Pour Windows (bat) - si vous êtes sur Linux, changez en 'sh'
                    bat '''
                        echo "=== 🛠️ OUTILS DISPONIBLES SUR L'AGENT JENKINS ==="
                        echo ""
                        
                        echo "📦 BUILD TOOLS:"
                        java -version 2>&1 | findstr "version" && echo "✅ Java" || echo "❌ Java non installé"
                        mvn --version 2>&1 | findstr "Apache Maven" && echo "✅ Maven" || echo "❌ Maven non installé"
                        git --version 2>&1 | findstr "git version" && echo "✅ Git" || echo "❌ Git non installé"
                        
                        echo ""
                        echo "=== ✅ VÉRIFICATION TERMINÉE ==="
                    '''
                    
                    // Vérification des versions spécifiques
                    bat '''
                        echo ""
                        echo "=== 📋 VERSIONS DÉTAILLÉES ==="
                        java -version
                        echo ""
                        mvn --version
                        echo ""
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
                    echo 'Le pipeline continue mais certains stages pourraient échouer'
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
                    
                    // Checkout du code SANS credentials (dépôt public)
                    git branch: "${GIT_BRANCH}", 
                         url: "${GIT_URL}",
                         poll: false,
                         changelog: false
                    
                    // Afficher la structure du projet
                    bat '''
                        echo ""
                        echo "=== 📂 STRUCTURE DU PROJET ==="
                        echo "Projet: ${APP_NAME}"
                        dir /b
                        echo ""
                        echo "=== 📄 FICHIERS IMPORTANTS ==="
                        if exist pom.xml ( 
                            echo "✅ pom.xml" 
                            echo "Contenu de pom.xml (premières lignes):"
                            type pom.xml | findstr "<" | head -10
                        ) else ( 
                            echo "❌ pom.xml manquant" 
                        )
                        if exist src\\main\\java ( 
                            echo "✅ Code source Java" 
                            dir /b src\\main\\java
                        ) else ( 
                            echo "❌ Code source manquant" 
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
                        dir /s /c | find "bytes"
                        echo ""
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
                        echo ""
                        echo "Artifacts principaux:"
                        type pom.xml | findstr "<artifactId>" | findstr -v "filter" | head -5
                        echo ""
                    '''
                    
                    try {
                        // Installation sans tests pour aller plus vite
                        bat 'mvn clean dependency:resolve -DskipTests'
                        echo '✅ Dépendances résolues avec succès'
                        
                    } catch (Exception e) {
                        echo "⚠️ Erreur lors de la résolution des dépendances: ${e.message}"
                        echo "Tentative avec offline mode..."
                        
                        bat 'mvn clean dependency:go-offline -DskipTests'
                        echo '✅ Dépendances téléchargées en mode offline'
                    }
                    
                    echo "📊 Rapport des dépendances..."
                    bat '''
                        echo "=== 📋 CACHE MAVEN ==="
                        echo ""
                        if exist "%USERPROFILE%\\.m2\\repository" (
                            echo "Cache Maven trouvé"
                            echo "Taille approximative:"
                            dir /s "%USERPROFILE%\\.m2\\repository" | find "File(s)"
                        ) else (
                            echo "Cache Maven non trouvé"
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
                    echo 'Solutions possibles:'
                    echo '1. Vérifiez la connexion internet'
                    echo '2. Vérifiez les repositories Maven'
                    echo '3. Essayez: mvn dependency:purge-local-repository'
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
                            echo "✅ Classes compilées avec succès"
                            echo "Nombre de fichiers .class:"
                            dir /s /b target\\classes\\*.class 2>nul | find /c ".class" || echo "0"
                        ) else (
                            echo "❌ Aucune classe compilée - vérifiez les erreurs"
                        )
                    '''
                }
            }
            
            post {
                success {
                    echo '✅ Compilation réussie'
                    archiveArtifacts artifacts: '**/target/*.jar', fingerprint: false
                }
                failure {
                    echo '❌ Échec de compilation'
                    echo 'Vérifiez les erreurs de compilation dans les logs'
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
            echo "1. Vérification Outils"
            echo "2. Checkout Code"
            echo "3. Installation Dépendances"
            echo "4. Compilation"
            echo ""
            echo "📈 STATISTIQUES:"
            bat '''
                echo "Fichiers sources:"
                dir /s /b *.java 2>nul | find /c ".java" || echo "0"
                echo ""
                echo "Artifacts générés:"
                if exist target\\*.jar (
                    dir /b target\\*.jar
                ) else (
                    echo "Aucun JAR généré"
                )
            '''
        }
        
        success {
            echo '🎉 PIPELINE RÉUSSIE !'
            echo 'Toutes les étapes de build sont complétées avec succès'
            echo ''
            echo 'Prochaines étapes possibles:'
            echo '1. Ajouter les tests unitaires'
            echo '2. Ajouter SonarQube pour l\'analyse de code'
            echo '3. Construire l\'image Docker'
        }
        
        failure {
            echo '⚠️ PIPELINE ÉCHOUÉE'
            echo 'Consultez les logs pour identifier l\'erreur'
        }
    }
}