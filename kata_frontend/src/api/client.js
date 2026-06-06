const API_BASE = import.meta.env.VITE_API_BASE_URL ?? '';


async function request(method, path, body, options = {}) {
  const headers = { Accept: 'application/json', ...options.headers };
  let payload;
  if (body !== undefined) {
    headers['Content-Type'] = 'application/json';
    payload = JSON.stringify(body);
  }
  const res = await fetch(`${API_BASE}${path}`, {
    method,
    headers,
    credentials: 'include',
    body: payload,
    ...options,
  });

  const text = await res.text();
  let data = null;
  if (text) {
    try {
      data = JSON.parse(text);
    } catch {
      throw new Error('Invalid response from server');
    }
  }

  if (!res.ok) {
    const msg = data && typeof data === 'object' && 'message' in data
      ? String(data.message)
      : res.statusText;
    const err = new Error(msg);
    err.status = res.status;
    if (data && typeof data === 'object' && 'fieldErrors' in data) {
      err.fieldErrors = data.fieldErrors;
    }
    throw err;
  }
  return data;
}

export function get(path, options = {}) {
  return request('GET', path, undefined, options);
}

export function post(path, body, options = {}) {
  return request('POST', path, body, options);
}

export function put(path, body, options = {}) {
  return request('PUT', path, body, options);
}

export function patch(path, body, options = {}) {
  return request('PATCH', path, body, options);
}

export function del(path, options = {}) {
  return request('DELETE', path, undefined, options);
}