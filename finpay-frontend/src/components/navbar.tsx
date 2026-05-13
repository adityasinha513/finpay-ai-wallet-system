import Link from "next/link";

import { Button }
from "@/components/ui/button";

export function Navbar() {

  return (
    <header className="w-full border-b border-zinc-800 bg-black">

      <div className="mx-auto flex h-16 max-w-7xl items-center justify-between px-6">

        <Link
          href="/"
          className="text-2xl font-bold text-white"
        >
          FinPay
        </Link>

        <div className="flex items-center gap-4">

          <Link href="/login">
            <Button variant="outline">
              Login
            </Button>
          </Link>

          <Link href="/register">
            <Button>
              Register
            </Button>
          </Link>

        </div>

      </div>

    </header>
  );
}