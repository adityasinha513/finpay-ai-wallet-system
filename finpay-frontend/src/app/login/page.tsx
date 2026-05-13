import { Navbar }
from "@/components/navbar";

import { LoginForm }
from "@/features/auth/components/login-form";

export default function LoginPage() {

  return (
    <main className="min-h-screen bg-black text-white">

      <Navbar />

      <section className="flex items-center justify-center px-6 py-24">

        <LoginForm />

      </section>

    </main>
  );
}