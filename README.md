# JOBPICK — 프리랜서 매칭 플랫폼 (2차 프로젝트)
**JOBPICK**은 누구나 직관적으로 사용할 수 있는 **프리랜서–프로젝트 매칭 플랫폼**입니다.  
기존 플랫폼들이 **프리랜서(재능) 페이지**와 **프로젝트(구인) 페이지**를 분리해 전환 비용이 컸다면, JOBPICK은 **단일 흐름(UI)에서 구인/구직을 모두 빠르게** 다룰 수 있도록 설계했습니다. “글 하나 올리고, 바로 매칭·대화·계약까지”가 목표입니다.
> FE와 BE를 **분리 레포지토리**로 운영합니다. 이 문서는 전체 개요/역할/개발 규칙을 안내합니다.

## 레포지토리
- FE (Next.js): https://github.com/prgrms-aibe-devcourse/AIBE3_Project2_Team3_FE
- BE (Spring Boot): https://github.com/prgrms-aibe-devcourse/AIBE3_Project2_Team3_BE

## 데모/배포
- FE: https://jobpick-one.vercel.app
- BE: https://www.jobpick.store
- API 문서(Swagger): https://www.jobpick.store/swagger-ui/index.html

## 발표자료
[[바로가기 링크]](https://www.miricanvas.com/v2/design2/v/bb50abf7-626f-4c2f-8ec7-3e310cf5dbb7)

## ERD
[[ERD Cloud]](https://www.erdcloud.com/d/FGhdneC729FFkri6L)
<img width="2000" height="1154" alt="image" src="https://github.com/user-attachments/assets/50f215c2-cb03-492e-b1cc-781ad5af2051" />

## 아키텍처 개요
<img width="5097" height="3563" alt="image" src="https://github.com/user-attachments/assets/0e4ffde0-ab01-4038-a0d6-efbc165c0a74" />

- FE: Next.js(App Router) + Tailwind + shadcn/ui
- BE: Spring Boot 3 + JPA + PostgreSQL + Redis (Refresh Token)
- Infra: AWS **EC2** (Docker) + **ECR**(이미지 레지스트리), Vercel
- **Database**: AWS **RDS** (MySQL)
- **Storage/CDN**: S3

## Tech Stack
- Spring Boot 3.5.5, Java 25
- Spring Data JPA, Spring Security
- MySQL, Redis(Refresh Token)
- Gradle
- Swagger / Springdoc OpenAPI

## 보안/인증
- Access/Refresh 토큰 (RT: Redis), 토큰 로테이션
- CORS/Rate Limit(선택)

## 운영 팁
- Init SQL로 테스트 데이터 주입

## 핵심 유스케이스
- 구인/구직 게시 + 지원/제안
- 채팅
- 카테고리/스킬/지역(트리)

## 개발 규칙(요약)
- 브랜치: `main`(보호) / `develop` / `feat/*` / `fix/*`
- 커밋: Conventional Commits  
  - `feat:`, `fix:`, `docs:`, `refactor:`, `test:`, `chore:`, `ci:`, `build:`
- PR: 템플릿 사용
- 이슈: 템플릿 사용 (배경 → 작업내용 → 스크린샷(선택))
