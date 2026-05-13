"use client"

import * as React from "react"
import {
  Controller,
  FormProvider,
  useFormContext,
  type Control,
  type ControllerRenderProps,
  type FieldValues,
  type Path,
  type UseFormReturn,
} from "react-hook-form"

const FormFieldContext = React.createContext<{ name: string } | undefined>(undefined)

export function Form<TFormValues extends FieldValues>({
  children,
  ...props
}: UseFormReturn<TFormValues> & {
  children: React.ReactNode
}) {
  return (
    <FormProvider {...props}>
      {children}
    </FormProvider>
  )
}

export function FormField<TFormValues extends FieldValues>({
  control,
  name,
  render,
}: {
  control: Control<TFormValues>
  name: Path<TFormValues>
  render: (props: { field: ControllerRenderProps<TFormValues, Path<TFormValues>> }) => React.ReactNode
}) {
  return (
    <Controller
      control={control}
      name={name}
      render={({ field }) => (
        <FormFieldContext.Provider value={{ name: String(name) }}>
          {render({ field })}
        </FormFieldContext.Provider>
      )}
    />
  )
}

export function FormItem({
  className,
  children,
  ...props
}: React.HTMLAttributes<HTMLDivElement>) {
  return (
    <div className={className} {...props}>
      {children}
    </div>
  )
}

export function FormLabel({
  className,
  children,
  ...props
}: React.LabelHTMLAttributes<HTMLLabelElement>) {
  return (
    <label className={className} {...props}>
      {children}
    </label>
  )
}

export function FormControl({
  className,
  children,
  ...props
}: React.HTMLAttributes<HTMLDivElement>) {
  return (
    <div className={className} {...props}>
      {children}
    </div>
  )
}

export function FormMessage({
  className,
  ...props
}: React.HTMLAttributes<HTMLParagraphElement>) {
  const field = React.useContext(FormFieldContext)
  const { formState } = useFormContext()
  const error = field ? formState.errors[field.name as Path<FieldValues>] : undefined

  if (!error) {
    return null
  }

  return (
    <p className={className} {...props}>
      {String(error.message ?? "")}
    </p>
  )
}
