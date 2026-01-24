import NextAuth, { AuthOptions } from "next-auth";
import KeycloakProvider from "next-auth/providers/keycloak";
import { JWT } from "next-auth/jwt";

// Extend NextAuth types to include custom properties
declare module "next-auth" {
    interface Session {
        accessToken?: string;
        user: {
            id?: string;
            name?: string;
            email?: string;
            roles?: string[];
        };
    }
}

declare module "next-auth/jwt" {
    interface JWT {
        accessToken?: string;
        idToken?: string;
        roles?: string[];
    }
}

export const authOptions: AuthOptions = {
    providers: [
        KeycloakProvider({
            clientId: process.env.KEYCLOAK_CLIENT_ID || "",
            clientSecret: process.env.KEYCLOAK_CLIENT_SECRET || "",
            issuer: process.env.KEYCLOAK_ISSUER,
        }),
    ],
    callbacks: {
        async jwt({ token, account, profile }) {
            // Initial sign in
            if (account && profile) {
                token.accessToken = account.access_token;
                token.idToken = account.id_token;

                // Extract roles from Keycloak token
                // Keycloak stores roles in realm_access.roles
                const keycloakProfile = profile as any;
                token.roles = keycloakProfile?.realm_access?.roles || [];
            }
            return token;
        },
        async session({ session, token }) {
            // Add access token and roles to session
            session.accessToken = token.accessToken as string;

            if (session.user) {
                session.user.roles = token.roles as string[];
                session.user.id = token.sub;
            }

            return session;
        },
    },
    pages: {
        signIn: '/login',
        error: '/login',
    },
    session: {
        strategy: 'jwt',
    },
};

const handler = NextAuth(authOptions);

export { handler as GET, handler as POST };
