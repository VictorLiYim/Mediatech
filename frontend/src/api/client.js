import axios from "axios";

// Chemin relatif commun : les appels ci-dessous incluent déjà /users, /books, /events
// (ex. userApi.post("/users/login", ...)) donc baseURL s'arrête à /api.
// En dev (npm run dev), le proxy Vite (vite.config.js) redirige /api/* vers les services
// locaux ; derrière le reverse proxy Docker (httpd), c'est httpd qui route vers le bon
// conteneur. Le code du front n'a jamais besoin de connaître un port.
export const userApi = axios.create({
    baseURL: "/api",
    headers: { "Content-Type": "application/json" },
});

export const bookApi = axios.create({
    baseURL: "/api",
    headers: { "Content-Type": "application/json" },
});

export const eventApi = axios.create({
    baseURL: "/api",
    headers: { "Content-Type": "application/json" },
});

/**
 * Extrait un message lisible depuis une erreur axios.
 * Le backend renvoie un ApiError JSON via le GlobalExceptionHandler.
 */
export function extractErrorMessage(error, fallback = "Une erreur est survenue") {
    return error.response?.data?.message ?? error.message ?? fallback;
}