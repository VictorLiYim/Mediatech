import { userApi } from "./client";

/**
 * POST /users — création d'un compte.
 * @param {{userName: string, email: string, password: string}} payload
 * @returns {Promise<{id: number, userName: string, email: string}>}
 */
export async function register(payload) {
    const { data } = await userApi.post("/users", payload);
    return data;
}

/**
 * POST /users/login — vérification des identifiants.
 */
export async function login(payload) {
    const { data } = await userApi.post("/users/login", payload);
    return data;
}

/**
 * GET /users/{id} — profil.
 */
export async function getUser(id) {
    const { data } = await userApi.get(`/users/${id}`);
    return data;
}