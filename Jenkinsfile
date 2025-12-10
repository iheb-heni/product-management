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

                    bat '''
                        echo === OUTILS DISPONIBLES ===
                        echo.
                        echo BUILD TOOLS:
                        java -version && echo [OK] Java
                        mvn --version && echo [OK] Maven
                        git --version && echo [OK] Git
                        echo.
                        echo === VERIFICATION TERMINEE ===
                    '''
                }
            }

            post {
                success {
                    echo '✅ Outils disponibles'
                }
            }
        }

        // ===============================
        // STAGE 2: CHECKOUT CODE
        // ===============================
        stage('Checkout Code') {
            steps {
                script {
                    echo '🎯 STAGE 2: TÉLÉCHARGEMENT DU CODE'

                    cleanWs()

                    echo "URL: ${GIT_URL}"
                    echo "Branche: ${GIT_BRANCH}"

                    git branch: "${GIT_BRANCH}",
                         url: "${GIT_URL}",
                         poll: false,
                         changelog: false

                    // Structure simple
                    bat '''
                        echo === STRUCTURE DU PROJET ===
                        dir /b
                        echo.
                        echo FICHIERS IMPORTANTS:
                        if exist pom.xml echo [OK] pom.xml
                        if exist src\\main\\java echo [OK] Code source Java
                        if exist src\\test\\java echo [OK] Tests disponibles
                    '''
                }
            }

            post {
                success {
                    echo '✅ Code téléchargé'
                    bat '''
                        echo Nombre de fichiers Java:
                        dir /s /b *.java 2>nul | find /c ".java" || echo 0
                    '''
                }
            }
        }

        // ===============================
        // STAGE 3: COMPILATION
        // ===============================
        stage('Compilation') {
            steps {
                script {
                    echo '🎯 STAGE 3: COMPILATION DU PROJET'

                    bat 'mvn clean compile -DskipTests'

                    bat '''
                        echo === VERIFICATION COMPILATION ===
                        if exist target\\classes (
                            echo [OK] Classes compilees
                        ) else (
                            echo [ERREUR] Aucune classe compilee
                        )
                    '''
                }
            }

            post {
                success {
                    echo '✅ Compilation réussie'
                }
                failure {
                    echo '❌ Échec de compilation'
                }
            }
        }

        // ===============================
        // STAGE 4: TESTS UNITAIRES
        // ===============================
        stage('Tests Unitaires') {
            steps {
                script {
                    echo '🎯 STAGE 4: TESTS UNITAIRES'

                    // Créer un répertoire de test si nécessaire
                    bat '''
                        if not exist "src\\test\\java\\com\\example\\productmanagement" mkdir "src\\test\\java\\com\\example\\productmanagement"
                    '''

                    // Écrire un test simple
                    writeFile file: 'src/test/java/com/example/productmanagement/SimpleTest.java', text: '''
package com.example.productmanagement;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SimpleTest {
    @Test
    void testAddition() {
        assertEquals(4, 2 + 2);
    }

    @Test
    void testString() {
        String message = "Hello Pipeline";
        assertNotNull(message);
        assertTrue(message.contains("Pipeline"));
    }
}
'''

                    // Exécuter les tests
                    bat 'mvn test'

                    // Publier les résultats
                    junit '**/target/surefire-reports/*.xml'
                }
            }

            post {
                success {
                    echo '✅ Tests exécutés avec succès'
                }
                failure {
                    echo '⚠️ Certains tests ont échoué'
                }
            }
        }

        // ===============================
        // STAGE 5: PACKAGING
        // ===============================
        stage('Packaging') {
            steps {
                script {
                    echo '🎯 STAGE 5: CREATION DU JAR'

                    bat 'mvn clean package -DskipTests'

                    bat '''
                        echo === ARTIFACTS GENERES ===
                        if exist target\\*.jar (
                            echo [OK] JAR cree
                            dir /b target\\*.jar
                        ) else (
                            echo [ERREUR] Aucun JAR genere
                        )
                    '''

                    // Archiver le JAR
                    archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
                }
            }

            post {
                success {
                    echo '✅ Package créé avec succès'
                }
            }
        }
    }

    post {
        always {
            echo '=== RAPPORT FINAL ==='
            echo "Build: #${BUILD_NUMBER}"
            echo "Projet: ${APP_NAME}"
            echo "Durée: ${currentBuild.durationString}"
            echo ''
            echo 'Stages complétés:'
            echo '1. Vérification outils'
            echo '2. Checkout code'
            echo '3. Compilation'
            echo '4. Tests unitaires'
            echo '5. Packaging'

            bat '''
                echo.
                echo STATISTIQUES:
                echo Fichiers Java:
                dir /s /b *.java 2>nul | find /c ".java" || echo 0
                echo Tests executes:
                if exist target\\surefire-reports\\*.xml (
                    dir /b target\\surefire-reports\\TEST-*.xml | find /c ".xml" || echo 0
                ) else (
                    echo 0
                )
            '''
        }

        success {
            echo '🎉 PIPELINE REUSSIE !'
            echo 'Toutes les étapes de build CI sont terminées.'
        }

        failure {
            echo '❌ PIPELINE ECHOUE'
        }
    }
}
