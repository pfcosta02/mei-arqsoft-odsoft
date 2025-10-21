import http from 'k6/http';
import { check, sleep } from 'k6';

// Configuração do teste
export let options = {
  thresholds: {
    http_req_failed: ['rate<0.01'], // <1% de falhas
    http_req_duration: ['p(95)<3000'], // 95% das req < 3s
  },
  stages: [
    { duration: '0s', target: 500 },
    { duration: '2s', target: 500 },
    { duration: '0s', target: 1000 },
    { duration: '2s', target: 1000 },
    { duration: '0s', target: 1500 },
    { duration: '2s', target: 1500 },
    { duration: '0s', target: 2000 },
    { duration: '15s', target: 2000 },
  ],
};

// Variáveis
const host = __ENV.HOST || 'http://localhost:8080';
const baseUrl = __ENV.BASE_URL || '/api';
const loginEndpoint = `${host}${baseUrl}/public/login`;
const bookEndpoint = `${host}${baseUrl}/books/9789896379636`;

const credentials = JSON.stringify({
  username: 'pedro@gmail.com',
  password: 'Pedrodascenas!123',
});

// Faz login e devolve o token do header Authorization
function authenticate() {
  const res = http.post(loginEndpoint, credentials, {
    headers: { 'Content-Type': 'application/json' },
  });

  check(res, {
    'login successful': (r) => r.status === 200,
    
  });

  // Extrai o header "Authorization"
  const authHeader = res.headers['Authorization'] || res.headers['authorization'];

  return authHeader;
}

// Executado uma vez antes do teste
export function setup() {
  const token = authenticate();
  console.log(`Token recebido: ${token}`);
  return { token };
}

// Executado por cada VU
export default function (data) {
  console.log(`Data recebido: ${data.token}`);
  const res = http.get(bookEndpoint, {
    headers: {
      Authorization: `Bearer ${data.token}`,
    },
  });

  check(res, {
    'book retrieved': (r) => r.status === 200,
  });

  sleep(1);
}
