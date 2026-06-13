# Book Management API

Spring Boot + jOOQ + PostgreSQL を使用した書籍管理APIです。  
著者・書籍・中間テーブルを管理し、CRUDおよび検索機能を提供します。

---

# 技術スタック

- Java / Kotlin
- Spring Boot
- jOOQ
- PostgreSQL
- Flyway
- JUnit5
- Mockito

---

# ER概要

## テーブル

- authors
- books
- book_authors

## 制約

- price >= 0
- publish_status: 「出版済み」「未出版」
- 外部キー制約あり

---

# 環境構築

## 1. DB起動（Docker）

docker-compose up -d

## 2. DB作成

CREATE DATABASE mydatabase;

## 3. マイグレーション

Flywayにより自動実行されます。

V1_create_tables.sql
V2_alter_publish_status_check.sql

## 4. アプリ起動

./gradlew bootRun

## 5. テスト実行

./gradlew test

※Junitを使用してテストする。一部Postmanも使用。

## 注意事項

テスト実行時、IntelliJで日本語が文字化けしたする可能性がある。
その際は、Windows設定「UTF-8を使用する（ベータ）」を有効化する。