import { Navbar }
from "@/components/navbar";

export default function HomePage() {

  return (
    <main className="min-h-screen bg-black text-white">

      <Navbar />

      <section className="flex flex-col items-center justify-center px-6 py-32 text-center">

        <h1 className="max-w-4xl text-6xl font-bold leading-tight">

          AI-Powered Fintech Platform

        </h1>

        <p className="mt-6 max-w-2xl text-lg text-zinc-400">

          Secure digital wallet platform with
          AI-driven analytics, fraud monitoring,
          autonomous financial insights,
          and intelligent transaction systems.

        </p>

      </section>

    </main>
  );
}