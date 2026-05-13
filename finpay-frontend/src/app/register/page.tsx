import { Navbar }
from "@/components/navbar";

import { RegisterForm }
from "@/features/auth/components/register-form";

export default function RegisterPage() {

  return (
    <main className="min-h-screen bg-black text-white">

      <Navbar />

      <section className="flex min-h-screen items-center justify-center px-6 py-16">

        <RegisterForm />

      </section>

    </main>
  );
}