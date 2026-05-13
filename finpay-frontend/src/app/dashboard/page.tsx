import { Navbar }
from "@/components/navbar";

export default function DashboardPage() {

  return (
    <main className="min-h-screen bg-black text-white">

      <Navbar />

      <section className="p-10">

        <h1 className="text-4xl font-bold">
          Dashboard
        </h1>

        <p className="mt-4 text-zinc-400">
          Welcome to FinPay AI Platform
        </p>

      </section>

    </main>
  );
}