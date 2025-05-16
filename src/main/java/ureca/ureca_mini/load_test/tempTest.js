import http from 'k6/http';
import { check, sleep } from 'k6';


//성공

export const options = {
    // 분당 10,000개의 요청 = 초당 약 166.67개
    scenarios: {
        constant_request_rate: {
            executor: 'constant-arrival-rate',
            rate: 5, // 10,000개의 요청을 timeUnit동안 (비율)
            timeUnit: '1s',   // 초 단위로 비율 지정
            duration: '1s',   // 테스트 지속 시간 (5분)
            preAllocatedVUs: 2000, // 미리 할당할 가상 사용자 수
            maxVUs: 4000,      // 필요시 최대 가상 사용자 수
        },
    },
    // thresholds: {
    //     http_req_failed: ['rate<0.01'], // 실패율 1% 미만 유지
    //     http_req_duration: ['p(95)<500'], // 95% 요청이 500ms 이내 완료
    // },
};

export default function() {
    // JSON 데이터 정의
    const payload = JSON.stringify({
        "eventId": 1,
        "userId": __VU,
    });

    // HTTP 헤더 정의 - Content-Type을 application/json으로 설정
    const params = {
        headers: {
            'Content-Type': 'application/json',
            // 필요한 경우 추가 헤더를 여기에 추가할 수 있습니다
            // 'Authorization': 'Bearer token123',
        },
    };

    // POST 요청으로 변경하고 payload와 params 추가
    //const res = http.post('http://localhost:8080/api/entry/kafka/v1', payload, params);
    const res = http.post('http://localhost:8080/api/entry/kafka/temp');

    // 응답 검증
    check(res, {
        'status is 200': (r) => r.status === 200,
        'response time < 200ms': (r) => r.timings.duration < 200,
    });
}