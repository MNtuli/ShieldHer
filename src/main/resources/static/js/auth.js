function showError(el, text) { el.textContent = text; el.hidden = false; }

async function submitAuth(form, path, buildBody) {
  const errorEl = document.getElementById('error');
  const btn = form.querySelector('button[type=submit]');
  errorEl.hidden = true;
  btn.disabled = true;
  try {
    const res = await apiFetch(path, { method: 'POST', body: buildBody(), auth: false });
    if (res.ok) {
      saveSession(res.data);
      window.location.replace('dashboard.html');
      return;
    }
    showError(errorEl, errorText(res.data, 'Something went wrong. Please try again.'));
  } catch (e) {
    showError(errorEl, 'Could not reach the server. Please check your connection and try again.');
  } finally {
    btn.disabled = false;
  }
}

const loginForm = document.getElementById('login-form');
if (loginForm) {
  loginForm.addEventListener('submit', ev => {
    ev.preventDefault();
    submitAuth(loginForm, '/api/auth/login', () => ({
      email: loginForm.email.value.trim(),
      password: loginForm.password.value
    }));
  });
}

const registerForm = document.getElementById('register-form');
if (registerForm) {
  registerForm.addEventListener('submit', ev => {
    ev.preventDefault();
    submitAuth(registerForm, '/api/auth/register', () => ({
      fullName: registerForm.fullName.value.trim(),
      email: registerForm.email.value.trim(),
      password: registerForm.password.value,
      phoneNumber: registerForm.phoneNumber.value.trim()
    }));
  });
}
